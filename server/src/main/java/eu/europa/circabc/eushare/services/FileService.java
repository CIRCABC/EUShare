/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.services;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.slf4j.LoggerFactory;
import org.sonarsource.scanner.api.internal.shaded.okhttp.Dns;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import eu.europa.circabc.eushare.configuration.EushareConfiguration;
import eu.europa.circabc.eushare.exceptions.CouldNotAllocateFileException;
import eu.europa.circabc.eushare.exceptions.CouldNotSaveFileException;
import eu.europa.circabc.eushare.exceptions.DateLiesInPastException;
import eu.europa.circabc.eushare.exceptions.EmptyFilenameException;
import eu.europa.circabc.eushare.exceptions.FileLargerThanAllocationException;
import eu.europa.circabc.eushare.exceptions.IllegalFileSizeException;
import eu.europa.circabc.eushare.exceptions.IllegalFileStateException;
import eu.europa.circabc.eushare.exceptions.MessageTooLongException;
import eu.europa.circabc.eushare.exceptions.UnknownFileException;
import eu.europa.circabc.eushare.exceptions.UnknownUserException;
import eu.europa.circabc.eushare.exceptions.UserHasInsufficientSpaceException;
import eu.europa.circabc.eushare.exceptions.UserUnauthorizedException;
import eu.europa.circabc.eushare.exceptions.WrongEmailStructureException;
import eu.europa.circabc.eushare.exceptions.WrongNameStructureException;
import eu.europa.circabc.eushare.exceptions.WrongPasswordException;
import eu.europa.circabc.eushare.model.FileInfoRecipient;
import eu.europa.circabc.eushare.model.FileInfoUploader;
import eu.europa.circabc.eushare.model.Recipient;
import eu.europa.circabc.eushare.storage.DBFile;
import eu.europa.circabc.eushare.storage.DBFileLog;
import eu.europa.circabc.eushare.storage.DBUser;
import eu.europa.circabc.eushare.storage.FileLogsRepository;
import eu.europa.circabc.eushare.storage.DBShare;
import eu.europa.circabc.eushare.storage.FileRepository;
import eu.europa.circabc.eushare.storage.MountPoint;
import eu.europa.circabc.eushare.storage.ShareRepository;
import eu.europa.circabc.eushare.storage.UserRepository;
import eu.europa.circabc.eushare.storage.DBUser.Role;
import eu.europa.circabc.eushare.utils.StringUtils;

/**
 * Service for managing all files that are available in the application. Use
 * this class exclusively to access files.
 */
@Service
public class FileService implements FileServiceInterface {

    private Logger log = LoggerFactory.getLogger(FileService.class);

    private final List<MountPoint> mountPoints = new ArrayList<>();

    @Autowired
    private EushareConfiguration esConfig;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileLogsRepository fileLogsRepository;

    @Autowired
    private ShareRepository shareRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * Prepare all given paths.
     */
    @PostConstruct
    private void initialize() throws IOException {
        for (String path : esConfig.getDisks()) {
            MountPoint mountPoint = new MountPoint(Paths.get(path));
            this.mountPoints.add(mountPoint);
        }
    }

    /**
     * Retrieve all files that are marked as deleted and physically removes them
     * from the file system.
     */
    @Scheduled(fixedDelay = 5000) // Every 5 seconds
    @Transactional
    void cleanupFiles() {
        for (DBFile file : fileRepository.findByStatus(DBFile.Status.DELETED, PageRequest.of(0, Integer.MAX_VALUE))) {
            log.info("Launching deletion of file id {}", file.getId());
            boolean fileDeletion = false;
            Path path = Paths.get(file.getPath());
            try {
                fileDeletion = Files.deleteIfExists(path);
                if (fileDeletion) {
                    for (DBShare dbShare : shareRepository.findByFile_id(file.getId())) {
                        shareRepository.delete(dbShare);
                    }
                    fileRepository.delete(file);
                }
            } catch (IOException e) {
                log.error("Could not delete DBFile, try again in next run", e);
            } finally {
                if (!fileDeletion) {
                    log.error("Could not delete file at path {}", path.toAbsolutePath());
                }
            }
        }
    }

    /**
     * Marks all expired files as deleted in the database.
     */
    @Scheduled(fixedDelay = 10000) // Every 10 seconds
    @Transactional
    void markExpiredFiles() {
        for (DBFile file : fileRepository.findByExpirationDateBefore(LocalDate.now())) {
            log.info("DBFile %s expired{}", file.getId());
            file.setStatus(eu.europa.circabc.eushare.storage.DBFile.Status.DELETED);
            fileRepository.save(file);
        }
    }

    /**
     * A copy of the {@code mountPoints} property.
     */
    private List<MountPoint> getMountPoints() {
        return new ArrayList<>(this.mountPoints);
    }

    /**
     * Tries to reserve space on any disk for the given file with size
     * {@code filesize}. On success, the path to this file will be returned,
     * otherwise {@code Optional.empty()}.
     */
    private Optional<String> tryReserveSpace(String id, long filesize) {
        List<MountPoint> mounts = this.getMountPoints();
        mounts.sort(Comparator.comparingLong(MountPoint::getTotalSpace));

        for (MountPoint m : mounts) {
            Optional<String> path = m.tryReserveSpace(id, filesize);

            if (path.isPresent()) {
                return path;
            }
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public void removeShareOnFileOnBehalfOf(String fileId, String userEmail, String requesterId)
            throws UnknownUserException, UnknownFileException, UserUnauthorizedException {
        if (this.isRequesterTheOwnerOfTheFileOrIsAnAdmin(fileId, requesterId)) {
            shareRepository.deleteByEmailAndFile_id(userEmail, fileId);
        } else {
            throw new UserUnauthorizedException();
        }
    }

    private boolean isRequesterTheOwnerOfTheFileOrIsAnAdmin(String fileId, String requesterId)
            throws UnknownFileException, UnknownUserException {
        DBFile dbFile = findFile(fileId);
        return dbFile.getUploader().getId().equals(requesterId) || userService.isAdmin(requesterId);
    }

    @Transactional
    @Override
    public Recipient addShareOnFileOnBehalfOf(String fileId, Recipient recipient, String requesterId)
            throws UserUnauthorizedException, UnknownUserException, WrongEmailStructureException,
            WrongNameStructureException, MessageTooLongException, UnknownFileException, MessagingException {
        if (this.isRequesterTheOwnerOfTheFileOrIsAnAdmin(fileId, requesterId)) {
            if (!StringUtils.validateMessage(recipient.getMessage())) {
                throw new MessageTooLongException();
            }
            DBFile dbFile = findAvailableFile(fileId, false);

            DBShare dbShare = new DBShare(recipient.getEmail(), dbFile, recipient.getMessage());

            String shortUrl;
            do {
                shortUrl = dbShare.generateShortUrl();
            } while (shareRepository.findOneByShorturl(shortUrl) != null);
            dbShare.setShorturl(shortUrl);

            shareRepository.save(dbShare);

            emailService.sendShareNotification(recipient.getEmail(), dbFile.toFileInfoRecipient(recipient.getEmail()),
                    recipient.getMessage());

            return dbShare.toRecipient();
        } else {
            throw new UserUnauthorizedException();
        }
    }

    /**
     * Tries to allocate space on disk for file with given size. If the allocation
     * fails, throws a corresponding Exception
     * 
     * @return File ID if allocation successful
     * @throws WrongNameStructureException
     */
    @Override
    @Transactional
    public String allocateFileOnBehalfOf(LocalDate expirationDate, String fileName, String password, String uploaderId,
            List<Recipient> recipientList, long filesize, String requesterId)
            throws DateLiesInPastException, IllegalFileSizeException, UserUnauthorizedException,
            UserHasInsufficientSpaceException, CouldNotAllocateFileException, UnknownUserException,
            EmptyFilenameException, WrongEmailStructureException, WrongNameStructureException, MessageTooLongException {

        // Validate uploader rights
        if (!uploaderId.equals(requesterId)) {
            DBUser possibleAdminUser = userService.getDbUser(requesterId);
            if (!possibleAdminUser.getRole().equals(Role.ADMIN)) {
                throw new UserUnauthorizedException();
            }
        }

        if (fileName == null) {
            throw new EmptyFilenameException();
        }

        // Validate fileSize and actual size
        if (filesize < 1) {
            throw new IllegalFileSizeException();
        }

        // Validate expirationDate
        if (expirationDate.isBefore(LocalDate.now())) {
            throw new DateLiesInPastException();
        }

        DBUser uploader = userService.getDbUser(uploaderId);

        if (uploader.getRole().equals(DBUser.Role.EXTERNAL)) {
            throw new UserUnauthorizedException();
        }

        if (uploader.getFreeSpace() < filesize) {
            throw new UserHasInsufficientSpaceException();
        }

        List<DBShare> recipientDBUserList = new LinkedList<>();
        DBFile dbFile = new DBFile(uploader, new HashSet<>(recipientDBUserList), fileName, filesize, expirationDate,
                "path", password);
        fileRepository.save(dbFile);
        String generatedFileId = dbFile.getId();

        String path = this.tryReserveSpace(generatedFileId, filesize).orElse(null);
        if (path == null) {
            fileRepository.delete(dbFile);
            throw new CouldNotAllocateFileException();
        }

        dbFile.setPath(path);
        fileRepository.save(dbFile);

        for (Recipient recipient : recipientList) {

            if (!StringUtils.validateMessage(recipient.getMessage())) {
                throw new MessageTooLongException();
            }
            DBShare dbShare = new DBShare(recipient.getEmail(), dbFile, recipient.getMessage());

            String shortUrl;
            do {
                shortUrl = dbShare.generateShortUrl();
            } while (shareRepository.findOneByShorturl(shortUrl) != null);
            dbShare.setShorturl(shortUrl);

            shareRepository.save(dbShare);
        }
        return generatedFileId;
    }

    private DBFile findFile(String fileId) throws UnknownFileException {
        DBFile f = fileRepository.findById(fileId).orElse(null);
        if (f == null || f.getStatus() == DBFile.Status.DELETED) {
            throw new UnknownFileException();
        }
        return f;
    }

    private DBFile findAvailableFile(String fileId, boolean isNotUploaderOrAdmin) throws UnknownFileException {
        DBFile f;
        if (!isNotUploaderOrAdmin) {
            f = fileRepository.findByStatusAndId(DBFile.Status.AVAILABLE, fileId);
        } else {
            f = fileRepository.findByStatusAndSharedWith_DownloadId(DBFile.Status.AVAILABLE, fileId);
        }
        if (f == null) {
            throw new UnknownFileException();
        }
        return f;
    }

    public DBFile findAvailableFileByShortUrl(String shortUrl)
            throws UnknownFileException {
        DBFile f;

        f = fileRepository.findByStatusAndSharedWith_Shorturl(DBFile.Status.AVAILABLE, shortUrl);

        if (f == null) {
            throw new UnknownFileException();
        }
        return f;
    }

    /**
     * Mark a file as deleted, sends a mail with the reason if {@code reason} is not
     * null to {@code requesterId}
     */
    @Override
    @Transactional
    public void deleteFileOnBehalfOf(String fileId, String reason, String requesterId)
            throws UnknownFileException, UserUnauthorizedException, UnknownUserException {
        DBFile f = fileRepository.findById(fileId).orElse(null);

        if (f == null || f.getStatus().equals(DBFile.Status.DELETED)) {
            throw new UnknownFileException();
        }

        if (!requesterId.equals(f.getUploader().getId()) && !userService.isAdmin(requesterId)) {
            throw new UserUnauthorizedException();
        }

        f.setStatus(DBFile.Status.DELETED);
        fileRepository.save(f);

        if (reason != null) {
            try {
                emailService.sendFileDeletedNotification(f.getUploader().getEmail(), f.toFileBasics(), reason);
            } catch (MessagingException ignored) {
                log.warn("Error while sending file deleted mail", ignored);
            }
        }
    }


    @Override
    @Transactional
    public void updateFileOnBehalfOf(String fileId, LocalDate expirationDate, String requesterId)
            throws UnknownFileException, UserUnauthorizedException, UnknownUserException {
        DBFile f = fileRepository.findById(fileId).orElse(null);

        if (f == null || f.getStatus().equals(DBFile.Status.DELETED)) {
            throw new UnknownFileException();
        }

        if (!requesterId.equals(f.getUploader().getId()) && !userService.isAdmin(requesterId)) {
            throw new UserUnauthorizedException();
        }
        f.setExpirationDate(expirationDate);
    
        fileRepository.save(f);

       
    }
    

    /**
     * Download the given file via the given session. Fails if the user may not
     * access the file, the file is unknown or the given password is wrong.
     * 
     * @throws UnknownFileException
     * @throws WrongPasswordException
     */
    @Override
    @Transactional
    public DownloadReturn downloadFile(String fileId, String password)
            throws WrongPasswordException, UnknownFileException {

        DBFile dbFile;

        DBShare dbShare;
        if (fileId.length() < 10) {
            dbShare = shareRepository.findOneByShorturl(fileId);
        } else {
            dbShare = shareRepository.findOneByDownloadId(fileId);
        }

        if (dbShare == null) {
            // File is downloaded by its uploader

            if (fileId.length() < 10) {
                dbFile = findAvailableFileByShortUrl(fileId);
            } else {
                dbFile = findAvailableFile(fileId, false);
            }

        } else {
            // File is downloaded by a user it is shared with
            dbFile = dbShare.getFile();
            if (!dbFile.getStatus().equals(DBFile.Status.AVAILABLE)) {
                throw new UnknownFileException();
            }
            String userIdentifier = dbShare.getEmail();

            try {
                this.emailService.sendDownloadNotification(dbFile.getUploader().getEmail(), userIdentifier,
                        dbFile.toFileBasics());
            } catch (MessagingException | ConnectException e) {
                log.error("Error happened when sending download notification for file " + fileId, e);
            }
        }
        if (dbFile.getPassword() != null && !BCrypt.checkpw(password, dbFile.getPassword())) {
            throw new WrongPasswordException();
        }
        File file = Paths.get(dbFile.getPath()).toFile();

        DBFileLog fileLogs;
        if(dbShare==null) {
         fileLogs = new DBFileLog(dbFile, "owner", LocalDateTime.now(),"");
        }
        else{
         fileLogs = new DBFileLog(dbFile, dbShare.getEmail(), LocalDateTime.now(), dbShare.getShorturl());
        }
        fileLogsRepository.save(fileLogs);


        return new DownloadReturn(file, dbFile.getFilename(), dbFile.getSize());
    }





    @Override
    @Transactional
    public List<FileInfoRecipient> getFileInfoRecipientOnBehalfOf(int pageSize, int pageNumber, String userId,
            String requesterId) throws UserUnauthorizedException, UnknownUserException {
        if (userService.isRequesterIdEqualsToUserIdOrIsAnAdmin(userId, requesterId)) {
            if (userService.isUserExists(userId)) {
                DBUser user = userRepository.findOneById(userId);
                String email = user.getEmail();
                return fileRepository
                        .findByStatusAndSharedWith_EmailOrderByExpirationDateAscFilenameAsc(DBFile.Status.AVAILABLE,
                                email, PageRequest.of(pageNumber, pageSize))
                        .stream().map(dbFile -> dbFile.toFileInfoRecipient(requesterId)).collect(Collectors.toList());
            } else {
                throw new UnknownUserException();
            }
        } else {
            throw new UserUnauthorizedException();
        }
    }

    @Override
    @Transactional
    public List<FileInfoUploader> getFileInfoUploaderOnBehalfOf(int pageSize, int pageNumber, String userId,
            String requesterId) throws UserUnauthorizedException, UnknownUserException {
        if (userService.isRequesterIdEqualsToUserIdOrIsAnAdmin(userId, requesterId)) {
            if (userService.isUserExists(userId)) {
                return fileRepository
                        .findByStatusAndUploader_IdOrderByExpirationDateAscFilenameAsc(DBFile.Status.AVAILABLE, userId,
                                PageRequest.of(pageNumber, pageSize))
                        .stream().map(dbFile -> dbFile.toFileInfoUploader()).collect(Collectors.toList());
            } else {
                throw new UnknownUserException();
            }
        } else {
            throw new UserUnauthorizedException();
        }
    }

    @Override
    @Transactional
    public FileInfoUploader saveOnBehalfOf(String fileId, MultipartFile resource, String requesterId)
            throws UnknownFileException, IllegalFileStateException, FileLargerThanAllocationException,
            UserUnauthorizedException, CouldNotSaveFileException, IllegalFileSizeException, MessagingException {
        DBFile f = findFile(fileId);

        if (requesterId.equals(f.getUploader().getId())) {
            this.save(f, resource);
            return f.toFileInfoUploader();
        } else {
            throw new UserUnauthorizedException();
        }
    }

    /**
     * Saves a file the file will not be saved.
     */
    private void save(DBFile dbFile, MultipartFile file)
            throws IllegalFileStateException, UnknownFileException, FileLargerThanAllocationException,
            UserUnauthorizedException, CouldNotSaveFileException, IllegalFileSizeException, MessagingException {

        if (dbFile == null) {
            throw new NullPointerException("dbFile is null");
        }
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        if (dbFile.getStatus() != DBFile.Status.ALLOCATED) {
            throw new IllegalFileStateException();
        }

        if (file.getSize() > dbFile.getSize()) {
            throw new FileLargerThanAllocationException();
        }

        if (file.getSize() > esConfig.getMaxSizeAllowedInBytes()) {
            throw new IllegalFileSizeException();
        }

        dbFile.setStatus(DBFile.Status.UPLOADING);

        dbFile.setSize(file.getSize());

        fileRepository.save(dbFile);

        Path p = Paths.get(dbFile.getPath());

        try {
            file.transferTo(p.toFile());
        } catch (IOException e) {
            throw new CouldNotSaveFileException(e);
        }
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        for (DBShare recipient : dbFile.getSharedWith()) {
            String recipientEmail = recipient.getEmail();
            if (recipient != null && StringUtils.validateEmailAddress(recipientEmail)) {
                FileInfoRecipient fileInfoRecipient = dbFile.toFileInfoRecipient(recipientEmail);
                this.emailService.sendShareNotification(recipientEmail, fileInfoRecipient, recipient.getMessage());
            }
        }

    }

    public static class DownloadReturn {
        private final File file;// NOSONAR
        private final String filename;// NOSONAR
        private final Long fileSizeInBytes;// NOSONAR

        public DownloadReturn(File file, String filename, Long fileSizeInBytes) {
            this.file = file;
            this.filename = filename;
            this.fileSizeInBytes = fileSizeInBytes;
        }

        public File getFile() {
            return file;
        }

        public String getFilename() {
            return filename;
        }

        public Long getFileSizeInBytes() {
            return fileSizeInBytes;
        }

        @Override
        public String toString() {
            return "DownloadReturn [file=" + file + ", fileSizeInBytes=" + fileSizeInBytes + ", filename=" + filename
                    + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((file == null) ? 0 : file.hashCode());
            result = prime * result + ((fileSizeInBytes == null) ? 0 : fileSizeInBytes.hashCode());
            result = prime * result + ((filename == null) ? 0 : filename.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DownloadReturn other = (DownloadReturn) obj;
            if (file == null) {
                if (other.file != null)
                    return false;
            } else if (!file.equals(other.file))
                return false;
            if (fileSizeInBytes == null) {
                if (other.fileSizeInBytes != null)
                    return false;
            } else if (!fileSizeInBytes.equals(other.fileSizeInBytes))
                return false;
            if (filename == null) {
                if (other.filename != null)
                    return false;
            } else if (!filename.equals(other.filename))
                return false;
            return true;
        }

    }

}
