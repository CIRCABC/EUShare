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

import java.time.LocalDate;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

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
import eu.europa.circabc.eushare.services.FileService.DownloadReturn;

public interface FileServiceInterface {
        public void removeShareOnFileOnBehalfOf(String fileId, String userId, String requesterId)
                        throws UnknownUserException, UnknownFileException, UserUnauthorizedException;

        public Recipient addShareOnFileOnBehalfOf(String fileId, Recipient recipient, String requesterId)
                        throws UnknownFileException, UserUnauthorizedException, UnknownUserException,
                        WrongNameStructureException, WrongEmailStructureException, MessageTooLongException, MessagingException;

        public DownloadReturn downloadFile(String fileId, String password) throws UnknownFileException,
                        WrongPasswordException, UserUnauthorizedException, UnknownUserException;

        public List<FileInfoRecipient> getFileInfoRecipientOnBehalfOf(int pageSize, int pageNumber, String userId,
                        String requesterId) throws UserUnauthorizedException, UnknownUserException;

        public List<FileInfoUploader> getFileInfoUploaderOnBehalfOf(int pageSize, int pageNumber, String userId,
                        String requesterId) throws UserUnauthorizedException, UnknownUserException;

        public String allocateFileOnBehalfOf(LocalDate expirationDate, String fileName, String password,
                        String uploaderId, List<Recipient> recipientList, long filesize, String requesterId)
                        throws DateLiesInPastException, IllegalFileSizeException, UserUnauthorizedException,
                        UserHasInsufficientSpaceException, CouldNotAllocateFileException, UnknownUserException,
                        EmptyFilenameException, WrongNameStructureException, WrongEmailStructureException, MessageTooLongException;

        public FileInfoUploader saveOnBehalfOf(String fileId, MultipartFile multipartFile, String requesterId)
                        throws UnknownFileException, IllegalFileStateException, FileLargerThanAllocationException,
                        UserUnauthorizedException, CouldNotSaveFileException, EmptyFilenameException,
                        IllegalFileSizeException, MessagingException;

        public void deleteFileOnBehalfOf(String fileId, String reason, String requesterId)
                        throws UnknownFileException, UserUnauthorizedException, UnknownUserException;

 
}