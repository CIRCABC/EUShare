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

import java.time.LocalDate;
import java.util.List;

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
import com.circabc.easyshare.services.FileService.DownloadReturn;

import org.springframework.core.io.Resource;

public interface FileServiceInterface {
    public void removeShareOnFileOnBehalfOf(String fileId, String userId, String requesterId)
            throws UnknownUserException, UnknownFileException, UserUnauthorizedException;

    public void addShareOnFileOnBehalfOf(String fileId, Recipient recipient, String requesterId)
            throws UnknownFileException, UserUnauthorizedException, UnknownUserException, WrongEmailStructureException, MessageTooLongException;

    public DownloadReturn downloadOnBehalfOf(String fileId, String password, String requesterId)
            throws UnknownFileException, WrongPasswordException, UserUnauthorizedException, UnknownUserException;

    public FileInfoRecipient getFileInfoRecipientOnBehalfOf(String fileId, String requesterId)
            throws UnknownFileException, UserUnauthorizedException;

    public FileInfoUploader getFileInfoUploaderOnBehalfOf(String fileId, String requesterId)
            throws UnknownFileException, UserUnauthorizedException;

    public String allocateFileOnBehalfOf(LocalDate expirationDate, String fileName, String password, String uploaderId, List<Recipient> recipientList, long filesize,
            String requesterId) throws DateLiesInPastException, IllegalFileSizeException, UserUnauthorizedException,
            UserHasInsufficientSpaceException, CouldNotAllocateFileException, UnknownUserException, EmptyFilenameException, WrongEmailStructureException;

    public void saveOnBehalfOf(String fileId, Resource resource, String requesterId)
            throws UnknownFileException, IllegalFileStateException, FileLargerThanAllocationException,
            UserUnauthorizedException, CouldNotSaveFileException, EmptyFilenameException, IllegalFileSizeException;

    public void deleteFileOnBehalfOf(String fileId, String reason, String requesterId)
            throws UnknownFileException, UserUnauthorizedException, UnknownUserException;
}