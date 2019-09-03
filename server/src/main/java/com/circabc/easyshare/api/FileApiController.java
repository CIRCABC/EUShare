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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.circabc.easyshare.exceptions.MessageTooLongException;
import com.circabc.easyshare.exceptions.NoAuthenticationException;
import com.circabc.easyshare.exceptions.UnknownFileException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserHasInsufficientSpaceException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.exceptions.WrongEmailStructureException;
import com.circabc.easyshare.exceptions.WrongPasswordException;
import com.circabc.easyshare.model.Credentials;
import com.circabc.easyshare.model.FileRequest;
import com.circabc.easyshare.model.Recipient;
import com.circabc.easyshare.model.validation.FileRequestValidator;
import com.circabc.easyshare.model.validation.RecipientValidator;
import com.circabc.easyshare.services.FileService;
import com.circabc.easyshare.services.FileService.DownloadReturn;
import com.circabc.easyshare.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-06-11T15:56:18.878+02:00[Europe/Paris]")

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
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoAuthenticationException | WrongAuthenticationException exc) {
            log.debug("wrong authentication !");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, HttpErrorAnswerBuilder.build401EmptyToString(),
                    exc);
        } catch (UserUnauthorizedException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
        } catch (UnknownUserException | UnknownFileException exc3) {
            log.warn(exc3.getMessage(), exc3);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, HttpErrorAnswerBuilder.build404EmptyToString(),
                    exc3);
        }
    }

    @Override
    public ResponseEntity<Void> deleteFileSharedWithUser(@PathVariable("fileID") String fileID,
            @PathVariable("userID") String userID) {
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            fileService.removeShareOnFileOnBehalfOf(fileID, userID, requesterId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoAuthenticationException | WrongAuthenticationException exc) {
            log.debug("wrong authentication !");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, HttpErrorAnswerBuilder.build401EmptyToString(),
                    exc);
        } catch (UserUnauthorizedException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
        } catch (UnknownUserException exc2) {
            log.warn(exc2.getMessage(), exc2);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404UserNotFoundToString(), exc2);
        } catch (UnknownFileException exc3) {
            log.warn(exc3.getMessage(), exc3);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404FileNotFoundToString(), exc3);
        }
    }

    @Override
    public ResponseEntity<Resource> getFile(@PathVariable("fileID") String fileID,
            @RequestParam(value = "password", required = false) String password) {
        try {
            DownloadReturn downloadReturn = fileService.downloadFile(fileID, password);
            File file = downloadReturn.getFile();
            InputStream stream = new FileInputStream(file);
            InputStreamResource inputStreamResource = new InputStreamResource(stream);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_LENGTH, downloadReturn.getFileSizeInBytes().toString());
            ResponseEntity<Resource> responseEntity = new ResponseEntity<>(inputStreamResource, responseHeaders, HttpStatus.OK);
            return responseEntity;
        } catch (UnknownFileException exc3) {
            log.warn(exc3.getMessage(), exc3);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, HttpErrorAnswerBuilder.build404EmptyToString(),
                    exc3);
        } catch (WrongPasswordException exc4) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, HttpErrorAnswerBuilder.build401EmptyToString(),
                    exc4);
        } catch (FileNotFoundException exc5) {
            log.error(exc5.getMessage(), exc5);
            // should never occur
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpErrorAnswerBuilder.build500EmptyToString());
        }
    }

    @Override
    public ResponseEntity<String> postFileFileRequest(@RequestBody(required = false) FileRequest fileRequest) {
        if (!FileRequestValidator.validate(fileRequest)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HttpErrorAnswerBuilder.build400EmptyToString());
        }
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            String fileId = fileService.allocateFileOnBehalfOf(fileRequest.getExpirationDate(), fileRequest.getName(),
                    fileRequest.getPassword(), requesterId, fileRequest.getSharedWith(),
                    fileRequest.getSize().longValue(), requesterId);
            return new ResponseEntity<>(fileId, HttpStatus.OK);
        } catch (NoAuthenticationException | WrongAuthenticationException exc) {
            log.debug("wrong authentication !");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, HttpErrorAnswerBuilder.build401EmptyToString(),
                    exc);
        } catch (IllegalFileSizeException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403IllegalFileSizeToString(), exc);
        } catch (DateLiesInPastException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403DateLiesInPastToString(), exc);
        } catch (UserHasInsufficientSpaceException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403UserHasInsufficientSpaceToString(), exc);
        } catch (UserUnauthorizedException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
        } catch (EmptyFilenameException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403EmptyFileNameToString(), exc);
        } catch (WrongEmailStructureException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403WrongEmailStructureToString(), e);
        } catch (CouldNotAllocateFileException | UnknownUserException exc3) {
            log.error(exc3.getMessage(), exc3);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpErrorAnswerBuilder.build500EmptyToString());
        }
    }

    @Override
    public ResponseEntity<Void> postFileContent(@PathVariable("fileID") String fileID,
            @RequestBody(required = false) Resource body) {
        try {
            if ((body == null) || body.contentLength() == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        HttpErrorAnswerBuilder.build400EmptyToString());
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            // should never occur
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, HttpErrorAnswerBuilder.build500EmptyToString());
        }

        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            fileService.saveOnBehalfOf(fileID, body, requesterId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoAuthenticationException | WrongAuthenticationException exc) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    HttpErrorAnswerBuilder.build401EmptyToString(), exc);
        } catch (FileLargerThanAllocationException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            HttpErrorAnswerBuilder.build403FileLargerThanAllocationToString(), exc);
        } catch (IllegalFileSizeException exc ){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            HttpErrorAnswerBuilder.build403IllegalFileSizeToString(), exc);
        } catch (UserUnauthorizedException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
        } catch (UnknownFileException exc2) {
            log.warn(exc2.getMessage(), exc2);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc2);
        } catch (IllegalFileStateException | CouldNotSaveFileException exc3) {
            log.error(exc3.getMessage(), exc3);
            // should never occur
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, HttpErrorAnswerBuilder.build500EmptyToString());
        }
    }

    @Override
    public ResponseEntity<Void> postFileSharedWith(@PathVariable("fileID") String fileID, @RequestBody Recipient recipient) {
        if (!RecipientValidator.validate(recipient)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    HttpErrorAnswerBuilder.build400EmptyToString());
        }
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            fileService.addShareOnFileOnBehalfOf(fileID, recipient, requesterId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(NoAuthenticationException| WrongAuthenticationException exc) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
            HttpErrorAnswerBuilder.build401EmptyToString(), exc);
        } catch ( UserUnauthorizedException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
        } catch (UnknownFileException exc2) {
            log.warn(exc2.getMessage(), exc2);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc2);
        } catch (UnknownUserException exc3) {
            log.warn(exc3.getMessage(), exc3);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc3);
        } catch (MessageTooLongException | WrongEmailStructureException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    HttpErrorAnswerBuilder.build400EmptyToString(), e);
        }
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
