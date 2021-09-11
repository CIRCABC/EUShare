/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.services;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
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

import com.circabc.easyshare.configuration.EasyShareConfiguration;
import com.circabc.easyshare.exceptions.CouldNotAllocateFileException;
import com.circabc.easyshare.exceptions.CouldNotSaveFileException;
import com.circabc.easyshare.exceptions.DateLiesInPastException;
import com.circabc.easyshare.exceptions.EmptyFilenameException;
import com.circabc.easyshare.exceptions.FileLargerThanAllocationException;
import com.circabc.easyshare.exceptions.IllegalFileSizeException;
import com.circabc.easyshare.exceptions.IllegalFileStateException;
import com.circabc.easyshare.exceptions.MessageTooLongException;
import com.circabc.easyshare.exceptions.UnknownFileException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserHasInsufficientSpaceException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongEmailStructureException;
import com.circabc.easyshare.exceptions.WrongNameStructureException;
import com.circabc.easyshare.exceptions.WrongPasswordException;
import com.circabc.easyshare.model.FileInfoRecipient;
import com.circabc.easyshare.model.FileInfoUploader;
import com.circabc.easyshare.model.Recipient;
import com.circabc.easyshare.model.RecipientWithLink;
import com.circabc.easyshare.storage.DBFile;
import com.circabc.easyshare.storage.DBUser;
import com.circabc.easyshare.storage.DBUser.Role;
import com.circabc.easyshare.storage.DBUserFile;
import com.circabc.easyshare.storage.FileRepository;
import com.circabc.easyshare.storage.MountPoint;
import com.circabc.easyshare.storage.UserFileRepository;
import com.circabc.easyshare.storage.UserRepository;
import com.circabc.easyshare.utils.StringUtils;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for managing all files that are available in the application. Use
 * this class exclusively to access files.
 */
@Service
public class FileService implements FileServiceInterface {

    private Logger log = LoggerFactory.getLogger(FileService.class);

    private final List<MountPoint> mountPoints = new ArrayList<>();

    @Autowired
    private EasyShareConfiguration esConfig;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserFileRepository userFileRepository;

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
            log.info(String.format("Launching deletion of file id %s", file.getId()));
            boolean fileDeletion = false;
            Path path = Paths.get(file.getPath());
            try {
                fileDeletion = Files.deleteIfExists(path);
                if (fileDeletion) {
                    for (DBUserFile dbUserFile : userFileRepository.findByFile_id(file.getId())) {
                        DBUser receiver = dbUserFile.getReceiver();
                        userFileRepository.delete(dbUserFile);
                        if (receiver.getRole().equals(Role.EXTERNAL) || receiver.getRole().equals(Role.LINK)) {
                            userRepository.delete(receiver);
                        }
                    }
                    fileRepository.delete(file);
                }
            } catch (IOException e) {
                log.error("Could not delete DBFile, try again in next run", e);
            } finally {
                if (!fileDeletion) {
                    log.error("Could not delete file at path %s", path.toAbsolutePath());
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
            log.info(String.format("DBFile %s expired", file.getId()));
            file.setStatus(com.circabc.easyshare.storage.DBFile.Status.DELETED);
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
     * Generate a new, unique file ID.
     */
    private String generateNewFileId() {
        while (true) {
            String id = StringUtils.randomString();
            if (!fileRepository.existsById(id)) {
                return id;
            }
        }
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
    public void removeShareOnFileOnBehalfOf(String fileId, String userId, String requesterId)
            throws UnknownUserException, UnknownFileException, UserUnauthorizedException {
        if (this.isRequesterTheOwnerOfTheFileOrIsAnAdmin(fileId, requesterId)) {
            userFileRepository.deleteByReceiver_idAndFile_id(userId, fileId);
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
    public RecipientWithLink addShareOnFileOnBehalfOf(String fileId, Recipient recipient, String requesterId)
            throws UserUnauthorizedException, UnknownUserException, WrongEmailStructureException,
            WrongNameStructureException, MessageTooLongException, UnknownFileException, MessagingException {
        if (this.isRequesterTheOwnerOfTheFileOrIsAnAdmin(fileId, requesterId)) {
            if (!StringUtils.validateMessage(recipient.getMessage())) {
                throw new MessageTooLongException();
            }
            DBFile dbFile = findAvailableFile(fileId, false);
            DBUser dbRecipient = userService.getUserOrCreateExternalOrLinkUser(recipient);
            DBUserFile dbUserFile = new DBUserFile(StringUtils.randomString(), dbRecipient, dbFile,
                    recipient.getMessage());
            userFileRepository.save(dbUserFile);
            if (recipient.getSendEmail()) {
                emailService.sendShareNotification(recipient.getEmailOrName(),
                        dbFile.toFileInfoRecipient(dbRecipient.getId()), recipient.getMessage());
            }

            return dbUserFile.toRecipientWithLink();
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

        String generatedFileId = this.generateNewFileId();
        String path = this.tryReserveSpace(generatedFileId, filesize).orElse(null);
        if (path == null) {
            throw new CouldNotAllocateFileException();
        }

        List<DBUserFile> recipientDBUserList = new LinkedList<>();
        DBFile dbFile = new DBFile(generatedFileId, uploader, new HashSet<DBUserFile>(recipientDBUserList), fileName,
                filesize, expirationDate, path, password);
        fileRepository.save(dbFile);

        for (Recipient recipient : recipientList) {
            DBUser recipientDBUser = userService.getUserOrCreateExternalOrLinkUser(recipient);
            if (!StringUtils.validateMessage(recipient.getMessage())) {
                throw new MessageTooLongException();
            }
            DBUserFile dbUserFile = new DBUserFile(this.generateNewFileId(), recipientDBUser, dbFile,
                    recipient.getMessage());
            userFileRepository.save(dbUserFile);
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
        DBUserFile dbUserFile = userFileRepository.findOneByDownloadId(fileId);

        if (dbUserFile == null) {
            // File is downloaded by its uploader
            dbFile = findAvailableFile(fileId, false);
        } else {
            // File is downloaded by a user it is shared with
            dbFile = dbUserFile.getFile();
            if (!dbFile.getStatus().equals(DBFile.Status.AVAILABLE)) {
                throw new UnknownFileException();
            }
            String userIdentifier = dbUserFile.getReceiver().getEmail();
            if (userIdentifier == null) {
                userIdentifier = dbUserFile.getReceiver().getName();
            }
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
        return new DownloadReturn(file, dbFile.getFilename(), dbFile.getSize());
    }

    @Override
    @Transactional
    public List<FileInfoRecipient> getFileInfoRecipientOnBehalfOf(int pageSize, int pageNumber, String userId,
            String requesterId) throws UserUnauthorizedException, UnknownUserException {
        if (userService.isRequesterIdEqualsToUserIdOrIsAnAdmin(userId, requesterId)) {
            if (userService.isUserExists(userId)) {
                return fileRepository
                        .findByStatusAndSharedWith_Receiver_IdOrderByExpirationDateAscFilenameAsc(
                                DBFile.Status.AVAILABLE, userId, PageRequest.of(pageNumber, pageSize))
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
        for (DBUserFile recipient : dbFile.getSharedWith()) {
            DBUser dbRecipient = recipient.getReceiver();
            if (dbRecipient != null && dbRecipient.getEmail() != null
                    && StringUtils.validateEmailAddress(dbRecipient.getEmail())) {
                FileInfoRecipient fileInfoRecipient = dbFile.toFileInfoRecipient(dbRecipient.getId());
                this.emailService.sendShareNotification(dbRecipient.getEmail(), fileInfoRecipient,
                        recipient.getMessage());
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
