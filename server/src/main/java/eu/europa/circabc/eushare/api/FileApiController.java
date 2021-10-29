/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.web.bind.annotation.RequestPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import eu.europa.circabc.eushare.error.HttpErrorAnswerBuilder;
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
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.exceptions.WrongEmailStructureException;
import eu.europa.circabc.eushare.exceptions.WrongNameStructureException;
import eu.europa.circabc.eushare.exceptions.WrongPasswordException;
import eu.europa.circabc.eushare.model.FileInfoUploader;
import eu.europa.circabc.eushare.model.FileRequest;
import eu.europa.circabc.eushare.model.Recipient;
import eu.europa.circabc.eushare.model.RecipientWithLink;
import eu.europa.circabc.eushare.model.validation.FileRequestValidator;
import eu.europa.circabc.eushare.model.validation.RecipientValidator;
import eu.europa.circabc.eushare.services.FileService;
import eu.europa.circabc.eushare.services.UserService;
import eu.europa.circabc.eushare.services.FileService.DownloadReturn;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.easyShare.base-path:/auth/realms/dev/.well-known/OpenID-configuration}")
public class FileApiController implements FileApi {

    private static final Logger log = LoggerFactory.getLogger(FileApiController.class);
  

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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String requesterId = userService.getAuthenticatedUserId(authentication);
            fileService.deleteFileOnBehalfOf(fileID, reason, requesterId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (WrongAuthenticationException exc) {
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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String requesterId = userService.getAuthenticatedUserId(authentication);
            fileService.removeShareOnFileOnBehalfOf(fileID, userID, requesterId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (WrongAuthenticationException exc) {
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
            ResponseEntity<Resource> responseEntity = new ResponseEntity<>(inputStreamResource, responseHeaders,
                    HttpStatus.OK);
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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String requesterId = userService.getAuthenticatedUserId(authentication);
            String fileId = fileService.allocateFileOnBehalfOf(fileRequest.getExpirationDate(), fileRequest.getName(),
                    fileRequest.getPassword(), requesterId, fileRequest.getSharedWith(),
                    fileRequest.getSize().longValue(), requesterId);
            return new ResponseEntity<>(fileId, HttpStatus.OK);
        } catch (WrongAuthenticationException exc) {
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
        } catch (WrongNameStructureException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403WrongNameStructureToString(), e);
        } catch (MessageTooLongException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403MessageTooLongToString(), e);
        }

        catch (CouldNotAllocateFileException | UnknownUserException exc3) {
            log.error(exc3.getMessage(), exc3);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpErrorAnswerBuilder.build500EmptyToString());
        }
    }

    @Override
    public ResponseEntity<FileInfoUploader> postFileContent(@PathVariable("fileID") String fileID,
            @RequestPart("file") MultipartFile body) {
        if ((body == null) || body.getSize() == 0L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HttpErrorAnswerBuilder.build400EmptyToString());
        }

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String requesterId = userService.getAuthenticatedUserId(authentication);
            FileInfoUploader fileInfoUploader = fileService.saveOnBehalfOf(fileID, body, requesterId);
            return new ResponseEntity<>(fileInfoUploader, HttpStatus.OK);
        } catch (WrongAuthenticationException exc) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, HttpErrorAnswerBuilder.build401EmptyToString(),
                    exc);
        } catch (FileLargerThanAllocationException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403FileLargerThanAllocationToString(), exc);
        } catch (IllegalFileSizeException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403IllegalFileSizeToString(), exc);
        } catch (UserUnauthorizedException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
        } catch (UnknownFileException exc2) {
            log.warn(exc2.getMessage(), exc2);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, HttpErrorAnswerBuilder.build404EmptyToString(),
                    exc2);
        } catch (IllegalFileStateException | CouldNotSaveFileException | MessagingException exc3) {
            log.error(exc3.getMessage(), exc3);
            // should never occur
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpErrorAnswerBuilder.build500EmptyToString());
        }
    }

    @Override
    public ResponseEntity<RecipientWithLink> postFileSharedWith(@PathVariable("fileID") String fileID,
            @RequestBody Recipient recipient) {
        if (!RecipientValidator.validate(recipient)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HttpErrorAnswerBuilder.build400EmptyToString());
        }
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String requesterId = userService.getAuthenticatedUserId(authentication);
            RecipientWithLink recipientWithLink = fileService.addShareOnFileOnBehalfOf(fileID, recipient, requesterId);
            return new ResponseEntity<>(recipientWithLink, HttpStatus.OK);
        } catch (WrongAuthenticationException exc) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, HttpErrorAnswerBuilder.build401EmptyToString(),
                    exc);
        } catch (UserUnauthorizedException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
        } catch (UnknownFileException exc2) {
            log.warn(exc2.getMessage(), exc2);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, HttpErrorAnswerBuilder.build404EmptyToString(),
                    exc2);
        } catch (UnknownUserException exc3) {
            log.warn(exc3.getMessage(), exc3);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, HttpErrorAnswerBuilder.build404EmptyToString(),
                    exc3);
        } catch (MessageTooLongException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403MessageTooLongToString(), e);
        } catch (WrongNameStructureException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403WrongNameStructureToString(), e);
        } catch (WrongEmailStructureException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403WrongEmailStructureToString(), e);
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    HttpErrorAnswerBuilder.build500EmptyToString(), e);
        }
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
