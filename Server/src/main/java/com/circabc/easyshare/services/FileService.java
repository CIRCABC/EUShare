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
import com.circabc.easyshare.services.EmailService;
import com.circabc.easyshare.storage.DBFile;
import com.circabc.easyshare.storage.DBUser;
import com.circabc.easyshare.storage.FileRepository;
import com.circabc.easyshare.storage.MountPoint;
import com.circabc.easyshare.storage.DBUser.Role;
import com.circabc.easyshare.utils.ResourceMultipartFile;
import com.circabc.easyshare.utils.StringUtils;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

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
        for (DBFile DBFile : fileRepository.findByStatus(DBFile.Status.DELETED)) {
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
            fileRepository.saveAndFlush(DBFile);
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
     * Checks whether {@code userId} may access the given file. This is true if the
     * user is either the uploader, got access to the file through the uploader or
     * is an admin.
     */
    private boolean mayAccess(String userId, String fileId) throws UnknownFileException, UnknownUserException {
        return mayAccess(userService.getDbUser(userId),
                fileRepository.findById(fileId).orElseThrow(() -> new UnknownFileException()));
    }

    /**
     * Checks whether {@code userId} may access the given file. This is true if the
     * user is either the uploader, got access to the file through the uploader or
     * is an admin.
     */
    private boolean mayAccess(DBUser user, DBFile file) {
        String userId = user.getId();
        if (userId.equals(file.getUploader().getId()) || user.getRole() == DBUser.Role.ADMIN) {
            return true;
        }
        for (DBUser u : file.getSharedWith()) {
            if (userId.equals(u.getId())) {
                return true;
            }
        }
        return false;
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
            Set<DBUser> dbUserSet = dbFile.getSharedWith();
            if (dbUserSet.remove(userService.getDbUser(userId))) {
                dbFile.setSharedWith(dbUserSet);
                fileRepository.saveAndFlush(dbFile);
            }
        } else {
            throw new UserUnauthorizedException();
        }
    }

    boolean isRequesterTheOwnerOfTheFileOrIsAnAdmin(String fileId, String requesterId)
            throws UnknownFileException, UnknownUserException {
        DBFile dbFile = findFile(fileId);
        return (dbFile.getUploader().getId().equals(requesterId) || userService.isAdmin(requesterId));
    }

    @Override
    public void addShareOnFileOnBehalfOf(String fileId, Recipient recipient, String requesterId)
            throws UnknownFileException, UserUnauthorizedException, UnknownUserException, WrongEmailStructureException,
            MessageTooLongException {
        if (this.isRequesterTheOwnerOfTheFileOrIsAnAdmin(fileId, requesterId)) {
            DBFile dbFile = findAvailableFile(fileId);
            Set<DBUser> dbUserSet = dbFile.getSharedWith();
            if(recipient.getMessage() !=null && recipient.getMessage().length() >=400) {
                throw new MessageTooLongException();
            }
            if (dbUserSet.add(userService.getUserOrCreateExternalUser(recipient))) {
                dbFile.setSharedWith(dbUserSet);
                fileRepository.saveAndFlush(dbFile);
            }
        }  else {
            throw new UserUnauthorizedException();
        }     
    }

    /**
     * Tries to allocate space on disk for file with given size
     * 
     * @param list
     * 
     * @return File ID if allocation successful
     * @throws WrongEmailStructureException
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

        if(fileName == null) {
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

        List<DBUser> recipientDBUserList = new LinkedList<>();
        for (Recipient recipient: recipientList ) {
            recipientDBUserList.add(userService.getUserOrCreateExternalUser(recipient));
        }

        String generatedFileId = this.generateNewFileId();
        String path = this.tryReserveSpace(generatedFileId, filesize).orElse(null);

        if (path == null) {
            throw new CouldNotAllocateFileException();
        }

        DBFile dbFile = new DBFile(generatedFileId, uploader, recipientDBUserList, fileName, filesize,
                expirationDate, path, password);
        fileRepository.saveAndFlush(dbFile);
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
        fileRepository.saveAndFlush(f);

        if (reason != null && !userService.isAdmin(requesterId)) {
            try {
                emailService.sendFileDeletedNotification(f.getUploader().getId(), f.toFileBasics(), reason);
            } catch (MessagingException ignored) {
            }
        }
    }

    /**
     * Download the given file via the given session. Fails if the user may not
     * access the file, the file is unknown or the given password is wrong.
     */
    @Override
    public DownloadReturn downloadFile(String fileId, String password)
            throws UnknownFileException, WrongPasswordException, UserUnauthorizedException, UnknownUserException {
        DBFile dbFile = findAvailableFile(fileId);

        File file = Paths.get(dbFile.getPath()).toFile();

        if (dbFile.getPassword() != null && !BCrypt.checkpw(password, dbFile.getPassword())) {
            throw new WrongPasswordException();
        }

        try {
            emailService.sendDownloadNotification(dbFile.getUploader().getId(), userId, dbFile.toFileBasics());
        } catch (Exception ignored) {
        }

        return new DownloadReturn(file, dbFile.getFilename());
    }

    private FileInfoRecipient getFileInfoRecipient(String fileId) throws UnknownFileException {
        return findAvailableFile(fileId).toFileInfoRecipient();
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

    private FileInfoUploader getFileInfoUploader(String fileId) throws UnknownFileException {
        return findAvailableFile(fileId).toFileInfoUploader();
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

        fileRepository.saveAndFlush(dbFile);

        Path p = Paths.get(dbFile.getPath());

        try {
            file.transferTo(p.toFile());
        } catch (IOException e) {
            throw new CouldNotSaveFileException();
        }

        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.saveAndFlush(dbFile);
    }

    @Data
    public static class DownloadReturn {
        private final File file;
        private final String filename;
    }

    
}
