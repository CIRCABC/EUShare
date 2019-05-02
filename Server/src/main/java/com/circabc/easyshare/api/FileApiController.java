/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

import com.circabc.easyshare.error.HttpErrorAnswerBuilder;
import com.circabc.easyshare.exceptions.CouldNotAllocateFileException;
import com.circabc.easyshare.exceptions.CouldNotSaveFileException;
import com.circabc.easyshare.exceptions.DateLiesInPastException;
import com.circabc.easyshare.exceptions.EmptyFilenameException;
import com.circabc.easyshare.exceptions.FileLargerThanAllocationException;
import com.circabc.easyshare.exceptions.IllegalFileSizeException;
import com.circabc.easyshare.exceptions.IllegalFileStateException;
import com.circabc.easyshare.exceptions.NoAuthenticationException;
import com.circabc.easyshare.exceptions.UnknownFileException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserHasInsufficientSpaceException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.exceptions.WrongEmailStructureException;
import com.circabc.easyshare.exceptions.WrongPasswordException;
import com.circabc.easyshare.model.Credentials;
import com.circabc.easyshare.model.FileInfoRecipient;
import com.circabc.easyshare.model.FileInfoUploader;
import com.circabc.easyshare.model.FileRequest;
import com.circabc.easyshare.model.Recipient;
import com.circabc.easyshare.services.FileService;
import com.circabc.easyshare.services.UserService;
import com.circabc.easyshare.services.FileService.DownloadReturn;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-04-10T14:56:31.271+02:00[Europe/Paris]")

@Slf4j
@Controller
@RequestMapping("${openapi.easyShare.base-path:}")
public class FileApiController extends AbstractController implements FileApi {

    private final NativeWebRequest request;

    @Autowired
    public FileService fileService;

    @Autowired
    public UserService userService;

    @org.springframework.beans.factory.annotation.Autowired
    public FileApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public ResponseEntity<Void> deleteFile(@PathVariable("fileID") String fileID,
            @RequestParam(value = "reason", required = false) String reason) {
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            fileService.deleteFileOnBehalfOf(fileID, reason, requesterId);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (UserUnauthorizedException | NoAuthenticationException | WrongAuthenticationException exc) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
            throw responseStatusException;
        } catch (UnknownUserException | UnknownFileException exc3) {
            log.warn(exc3.getMessage(), exc3);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc3);
            throw responseStatusException;
        }
    }

    @Override
    public ResponseEntity<Void> deleteFileSharedWithUser(@PathVariable("fileID") String fileID,
            @PathVariable("userID") String userID) {
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            fileService.removeShareOnFileOnBehalfOf(fileID, userID, requesterId);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (UserUnauthorizedException | NoAuthenticationException | WrongAuthenticationException exc) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
            throw responseStatusException;
        } catch (UnknownUserException exc2) {
            log.warn(exc2.getMessage(), exc2);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404UserNotFoundToString(), exc2);
            throw responseStatusException;
        } catch (UnknownFileException exc3) {
            log.warn(exc3.getMessage(), exc3);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404FileNotFoundToString(), exc3);
            throw responseStatusException;
        }
    }

    @Override
    public ResponseEntity<Resource> getFile(@PathVariable("fileID") String fileID,
            @RequestParam(value = "password", required = false) String password) {
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            DownloadReturn downloadReturn = fileService.downloadOnBehalfOf(fileID, password, requesterId);
            File file = downloadReturn.getFile();
            InputStream stream = new FileInputStream(file);
            InputStreamResource inputStreamResource = new InputStreamResource(stream);
            return new ResponseEntity<Resource>(inputStreamResource, HttpStatus.OK);
        } catch (UserUnauthorizedException | NoAuthenticationException | WrongAuthenticationException exc) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
            throw responseStatusException;
        } catch (UnknownFileException exc3) {
            log.warn(exc3.getMessage(), exc3);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc3);
            throw responseStatusException;
        } catch (WrongPasswordException exc4) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    HttpErrorAnswerBuilder.build401EmptyToString(), exc4);
            throw responseStatusException;
        } catch (UnknownUserException | FileNotFoundException exc5) {
            log.error(exc5.getMessage(), exc5);
            // should never occur
            ResponseStatusException responseStatusException = new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, HttpErrorAnswerBuilder.build500EmptyToString());
            throw responseStatusException;
        }
    }

    @Override
    public ResponseEntity<FileInfoRecipient> getFileFileInfoRecipient(@PathVariable("fileID") String fileID) {
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            FileInfoRecipient fileInfoRecipient = fileService.getFileInfoRecipientOnBehalfOf(fileID, requesterId);
            return new ResponseEntity<FileInfoRecipient>(fileInfoRecipient, HttpStatus.OK);
        } catch (UserUnauthorizedException | NoAuthenticationException | WrongAuthenticationException exc) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
            throw responseStatusException;
        } catch (UnknownFileException exc2) {
            log.warn(exc2.getMessage(), exc2);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc2);
            throw responseStatusException;
        }
    }

    @Override
    public ResponseEntity<FileInfoUploader> getFileFileInfoUploader(@PathVariable("fileID") String fileID) {
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            FileInfoUploader fileInfoUploader = fileService.getFileInfoUploaderOnBehalfOf(fileID, requesterId);
            return new ResponseEntity<FileInfoUploader>(fileInfoUploader, HttpStatus.OK);
        } catch (UserUnauthorizedException | NoAuthenticationException | WrongAuthenticationException exc) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
            throw responseStatusException;
        } catch (UnknownFileException exc2) {
            log.warn(exc2.getMessage(), exc2);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc2);
            throw responseStatusException;
        }
    }

    @Override
    public ResponseEntity<String> postFileFileRequest(@RequestBody(required = false) FileRequest fileRequest) {
        if (fileRequest == null || fileRequest.getExpirationDate() == null || fileRequest.getHasPassword() == null
                || fileRequest.getName() == null || fileRequest.getSize() == null || fileRequest.getSharedWith() == null
                || fileRequest.getSharedWith().isEmpty()) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    HttpErrorAnswerBuilder.build400EmptyToString());
            throw responseStatusException;
        }

        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            String fileId = fileService.allocateFileOnBehalfOf(fileRequest.getExpirationDate(), fileRequest.getName(),
                    fileRequest.getPassword(), requesterId, fileRequest.getSharedWith(), fileRequest.getSize().longValue(), requesterId);
            return new ResponseEntity<String>(fileId, HttpStatus.OK);
        } catch (IllegalFileSizeException | DateLiesInPastException | UserHasInsufficientSpaceException
                | UserUnauthorizedException | NoAuthenticationException | WrongAuthenticationException exc) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
            throw responseStatusException;
        } catch (UnknownUserException exc2) {
            log.warn(exc2.getMessage(), exc2);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc2);
            throw responseStatusException;
        } catch (EmptyFilenameException | CouldNotAllocateFileException exc3) {
            log.error(exc3.getMessage(), exc3);
            // should never occur
            ResponseStatusException responseStatusException = new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, HttpErrorAnswerBuilder.build500EmptyToString());
            throw responseStatusException;
        } catch (WrongEmailStructureException e) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    HttpErrorAnswerBuilder.build400EmptyToString());
            throw responseStatusException;
        }
    }

    @Override
    public ResponseEntity<Void> postFileContent(@PathVariable("fileID") String fileID,
            @RequestBody(required = false) Resource body) {
        if (body == null) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    HttpErrorAnswerBuilder.build400EmptyToString());
            throw responseStatusException;
        }

        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            fileService.saveOnBehalfOf(fileID, body, requesterId);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (FileLargerThanAllocationException | IllegalFileSizeException | UserUnauthorizedException
                | NoAuthenticationException | WrongAuthenticationException exc) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
            throw responseStatusException;
        } catch (UnknownFileException exc2) {
            log.warn(exc2.getMessage(), exc2);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc2);
            throw responseStatusException;
        } catch (IllegalFileStateException | CouldNotSaveFileException exc3) {
            log.error(exc3.getMessage(), exc3);
            // should never occur
            ResponseStatusException responseStatusException = new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, HttpErrorAnswerBuilder.build500EmptyToString());
            throw responseStatusException;
        }
    }

    @Override
    public ResponseEntity<Void> postFileSharedWith(@PathVariable("fileID") String fileID,
            @RequestBody(required = false) Recipient recipient) {
        if (recipient == null) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    HttpErrorAnswerBuilder.build400EmptyToString());
            throw responseStatusException;
        }
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            fileService.addShareOnFileOnBehalfOf(fileID, recipient, requesterId);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (WrongEmailStructureException | UserUnauthorizedException | NoAuthenticationException
                | WrongAuthenticationException exc) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
            throw responseStatusException;
        } catch (UnknownFileException exc2) {
            log.warn(exc2.getMessage(), exc2);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404FileNotFoundToString(), exc2);
            throw responseStatusException;
        } catch (UnknownUserException exc3) {
            log.warn(exc3.getMessage(), exc3);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404FileNotFoundToString(), exc3);
            throw responseStatusException;
        }
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
