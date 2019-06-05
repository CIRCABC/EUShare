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
import java.io.InputStream;
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
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

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
import com.circabc.easyshare.exceptions.WrongPasswordException;
import com.circabc.easyshare.model.FileInfoRecipient;
import com.circabc.easyshare.model.FileInfoUploader;
import com.circabc.easyshare.model.Recipient;
import com.circabc.easyshare.storage.DBFile;
import com.circabc.easyshare.storage.DBUser;
import com.circabc.easyshare.storage.DBUser.Role;
import com.circabc.easyshare.storage.DBUserFile;
import com.circabc.easyshare.storage.FileRepository;
import com.circabc.easyshare.storage.MountPoint;
import com.circabc.easyshare.storage.UserFileRepository;
import com.circabc.easyshare.utils.ResourceMultipartFile;
import com.circabc.easyshare.utils.StringUtils;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing all files that are available in the application. Use
 * this class exclusively to access files.
 */
@Service
@Slf4j
public class FileService implements FileServiceInterface {
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
    @Scheduled(fixedDelay = 5000)
    private void cleanupFiles() {
        for (DBFile DBFile : fileRepository.findByStatus(DBFile.Status.DELETED, PageRequest.of(0, Integer.MAX_VALUE))) {
            log.info(String.format("Trying to delete %s", DBFile.getId()));
            boolean fileDeletion = false;
            Path path = Paths.get(DBFile.getPath());
            try {
                fileDeletion = Files.deleteIfExists(path);
                fileRepository.delete(DBFile);
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
    @Scheduled(fixedRate = 10_000)
    private void markExpiredFiles() {
        for (DBFile DBFile : fileRepository.findByExpirationDateBefore(LocalDate.now())) {
            log.info(String.format("DBFile %s expired", DBFile.getId()));
            DBFile.setStatus(com.circabc.easyshare.storage.DBFile.Status.DELETED);
            fileRepository.save(DBFile);
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
    public void removeShareOnFileOnBehalfOf(String fileId, String userId, String requesterId)
            throws UnknownUserException, UnknownFileException, UserUnauthorizedException {
        if (this.isRequesterTheOwnerOfTheFileOrIsAnAdmin(fileId, requesterId)) {
            DBFile dbFile = findAvailableFile(fileId);
            DBUser userToRemove = userService.getDbUser(userId);
            Set<DBUserFile> sharedWith = dbFile.getSharedWith().stream()
                    .filter(dbUserFile -> dbUserFile.getReceiver().equals(userToRemove)).collect(Collectors.toSet());
            dbFile.setSharedWith(sharedWith);
            fileRepository.save(dbFile);
        } else {
            throw new UserUnauthorizedException();
        }
    }

    public boolean isRequesterTheOwnerOfTheFileOrIsAnAdmin(String fileId, String requesterId)
            throws UnknownFileException, UnknownUserException {
        DBFile dbFile = findFile(fileId);
        return dbFile.getUploader().getId().equals(requesterId) || userService.isAdmin(requesterId);
    }

    @Override
    public void addShareOnFileOnBehalfOf(String fileId, Recipient recipient, String requesterId)
            throws  UserUnauthorizedException, UnknownUserException, WrongEmailStructureException,
            MessageTooLongException, UnknownFileException {
        if (this.isRequesterTheOwnerOfTheFileOrIsAnAdmin(fileId, requesterId)) {
            DBFile dbFile = findAvailableFile(fileId);
            Set<DBUserFile> dbUserSet = dbFile.getSharedWith();
            if (recipient.getMessage() != null && recipient.getMessage().length() >= 400) {
                throw new MessageTooLongException();
            }
            DBUserFile dbUserFile = new DBUserFile(fileId, userService.getUserOrCreateExternalUser(recipient), dbFile,
                    recipient.getMessage());
            if (dbUserSet.add(dbUserFile)) {
                dbFile.setSharedWith(dbUserSet);
                fileRepository.save(dbFile);
            }
        } else {
            throw new UserUnauthorizedException();
        }
    }

    /**
     * Tries to allocate space on disk for file with given size.
     * If the allocation fails, the 
     * @return File ID if allocation successful
     */
    @Override
    public String allocateFileOnBehalfOf(LocalDate expirationDate, String fileName, String password, String uploaderId,
            List<Recipient> recipientList, long filesize, String requesterId) throws DateLiesInPastException,
            IllegalFileSizeException, UserUnauthorizedException, UserHasInsufficientSpaceException,
            CouldNotAllocateFileException, UnknownUserException, EmptyFilenameException, WrongEmailStructureException {

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
        DBFile dbFile = new DBFile(generatedFileId, uploader, null, fileName, filesize, expirationDate, path, password);
        for (Recipient recipient : recipientList) {
            DBUser recipientDBUser = userService.getUserOrCreateExternalUser(recipient);
            DBUserFile dbUserFile = new DBUserFile(this.generateNewFileId(), recipientDBUser, dbFile,
                    recipient.getMessage());
            recipientDBUserList.add(dbUserFile);
        }

        dbFile.setSharedWith(new HashSet<DBUserFile>(recipientDBUserList));
        fileRepository.save(dbFile);
        return generatedFileId;
    }

    private DBFile findFile(String fileId) throws UnknownFileException {
        DBFile f = fileRepository.findById(fileId).orElse(null);
        if (f == null || f.getStatus() == DBFile.Status.DELETED) {
            throw new UnknownFileException();
        }
        return f;
    }

    private DBFile findAvailableFile(String fileId) throws UnknownFileException {
        DBFile f = fileRepository.findById(fileId).orElse(null);
        if (f == null || f.getStatus() != DBFile.Status.AVAILABLE) {
            throw new UnknownFileException();
        }
        return f;
    }

    /**
     * Mark a file as deleted, sends a mail with the reason if {@code reason} is not
     * null to {@code requesterId}
     */
    @Override
    public void deleteFileOnBehalfOf(String fileId, String reason, String requesterId)
            throws UnknownFileException, UserUnauthorizedException, UnknownUserException {
        DBFile f = fileRepository.findById(fileId).orElse(null);

        if (f == null || f.getStatus() == DBFile.Status.DELETED) {
            throw new UnknownFileException();
        }

        if (!requesterId.equals(f.getUploader().getId()) && !userService.isAdmin(requesterId)) {
            throw new UserUnauthorizedException();
        }

        f.setStatus(DBFile.Status.DELETED);
        fileRepository.save(f);

        if (reason != null && !userService.isAdmin(requesterId)) {
            try {
                emailService.sendFileDeletedNotification(f.getUploader().getId(), f.toFileBasics(), reason);
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
    public DownloadReturn downloadFile(String fileId, String password)
            throws WrongPasswordException, UnknownFileException {
        DBUserFile dbUserFile = userFileRepository.findOneByDownloadId(fileId);
        if (dbUserFile == null) {
            throw new UnknownFileException();
        }
        DBFile dbFile = dbUserFile.getFile();
        if (!dbFile.getStatus().equals(DBFile.Status.AVAILABLE)) {
            throw new UnknownFileException();
        }
        if (dbFile.getPassword() != null && !BCrypt.checkpw(password, dbFile.getPassword())) {
            throw new WrongPasswordException();
        }
        File file = Paths.get(dbFile.getPath()).toFile();
        String userIdentifier = dbUserFile.getReceiver().getEmail();
        if (userIdentifier == null) {
            userIdentifier = dbUserFile.getReceiver().getName();
        }
        try {
            emailService.sendDownloadNotification(dbFile.getUploader().getId(), userIdentifier, dbFile.toFileBasics());
        } catch (MessagingException e) {
            log.error("Error happened when sending download notification for file " + fileId, e);
        }

        return new DownloadReturn(file, dbFile.getFilename());
    }

    @Override
    public FileInfoRecipient getFileInfoRecipientOnBehalfOf(String fileId, String requesterId)
            throws UnknownFileException, UserUnauthorizedException {
        DBFile f = findAvailableFile(fileId);
        if (requesterId.equals(f.getUploader().getId())) {
            return f.toFileInfoRecipient();
        } else {
            throw new UserUnauthorizedException();
        }
    }

    @Override
    public List<FileInfoRecipient> getFileInfoRecipientOnBehalfOf(int pageSize, int pageNumber, String userId, String requesterId)
    throws UserUnauthorizedException, UnknownUserException {
        if (userService.isRequesterIdEqualsToUserIdOrIsAnAdmin(userId, requesterId)) {
            return fileRepository.findByStatusAndSharedWith_Receiver_Id(DBFile.Status.AVAILABLE, userId, PageRequest.of(pageNumber, pageSize))
            .stream().map(dbFile -> dbFile.toFileInfoRecipient()).collect(Collectors.toList());
        } else {
            throw new UserUnauthorizedException();
        }
    }

    @Override
    public List<FileInfoUploader> getFileInfoUploaderOnBehalfOf(int pageSize, int pageNumber, String userId, String requesterId) throws UserUnauthorizedException, UnknownUserException {
        if (userService.isRequesterIdEqualsToUserIdOrIsAnAdmin(userId, requesterId)) {
            return fileRepository.findByStatusAndUploader_Id(DBFile.Status.AVAILABLE, userId, PageRequest.of(pageNumber, pageSize))
            .stream().map(dbFile -> dbFile.toFileInfoUploader()).collect(Collectors.toList());
        } else {
            throw new UserUnauthorizedException();
        }
    }

    @Override
    public FileInfoUploader getFileInfoUploaderOnBehalfOf(String fileId, String requesterId)
            throws UnknownFileException, UserUnauthorizedException {
        DBFile f = findAvailableFile(fileId);
        if (requesterId.equals(f.getUploader().getId())) {
            return f.toFileInfoUploader();
        } else {
            throw new UserUnauthorizedException();
        }
    }

    @Override
    public void saveOnBehalfOf(String fileId, Resource resource, String requesterId)
            throws UnknownFileException, IllegalFileStateException, FileLargerThanAllocationException,
            UserUnauthorizedException, CouldNotSaveFileException, IllegalFileSizeException {
        DBFile f = findFile(fileId);

        if (requesterId.equals(f.getUploader().getId())) {
            try {
                InputStream inputStream = resource.getInputStream();
                byte[] bytes = IOUtils.toByteArray(inputStream);
                ResourceMultipartFile resourceMultipartFile = new ResourceMultipartFile(resource,
                        resource.getFilename(), null, bytes.length);
                this.save(f, resourceMultipartFile);
            } catch (IOException io) {
                throw new CouldNotSaveFileException(io);
            }
        } else {
            throw new UserUnauthorizedException();
        }
    }

    /**
     * Saves a file the file will not be saved.
     */
    private void save(@NonNull DBFile dbFile, @NonNull MultipartFile file)
            throws IllegalFileStateException, UnknownFileException, FileLargerThanAllocationException,
            UserUnauthorizedException, CouldNotSaveFileException, IllegalFileSizeException {
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
    }

    @Data
    public static class DownloadReturn {
        private final File file;//NOSONAR
        private final String filename;//NOSONAR
    }

}
