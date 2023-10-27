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
import eu.europa.circabc.eushare.exceptions.UserHasNoUploadRightsException;
import eu.europa.circabc.eushare.exceptions.UserUnauthorizedException;
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.exceptions.WrongPasswordException;
import eu.europa.circabc.eushare.model.EnumConverter;
import eu.europa.circabc.eushare.model.FileBasics;
import eu.europa.circabc.eushare.model.FileInfoUploader;
import eu.europa.circabc.eushare.model.FileRequest;
import eu.europa.circabc.eushare.model.FileResult;
import eu.europa.circabc.eushare.model.FileStatusUpdate;
import eu.europa.circabc.eushare.model.InlineObject;
import eu.europa.circabc.eushare.model.Recipient;
import eu.europa.circabc.eushare.model.validation.FileRequestValidator;
import eu.europa.circabc.eushare.model.validation.RecipientValidator;
import eu.europa.circabc.eushare.security.CaptchaValidator;
import eu.europa.circabc.eushare.services.FileService;
import eu.europa.circabc.eushare.services.FileService.DownloadReturn;
import eu.europa.circabc.eushare.storage.entity.DBAbuse;
import eu.europa.circabc.eushare.storage.entity.DBFile;
import eu.europa.circabc.eushare.storage.entity.DBUser;
import eu.europa.circabc.eushare.storage.repository.FileRepository;
import eu.europa.circabc.eushare.services.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
public class FileApiController implements FileApi {

  private static final Logger log = LoggerFactory.getLogger(
      FileApiController.class);

  private final NativeWebRequest request;

  @Autowired
  public FileService fileService;

  @Autowired
  public FileRepository fileRepository;

  @Autowired
  public UserService userService;

  @Autowired
  private CaptchaValidator captchaValidator;

  @org.springframework.beans.factory.annotation.Autowired
  public FileApiController(NativeWebRequest request) {
    this.request = request;
  }

  @Override
  public ResponseEntity<Void> deleteFile(
      @PathVariable("fileID") String fileID,
      @RequestParam(value = "reason", required = false) String reason) {
    try {
      Authentication authentication = SecurityContextHolder
          .getContext()
          .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);
      fileService.deleteFileOnBehalfOf(fileID, reason, requesterId);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (WrongAuthenticationException exc) {
      log.debug("wrong authentication !");
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          HttpErrorAnswerBuilder.build401EmptyToString(),
          exc);
    } catch (UserUnauthorizedException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
          exc);
    } catch (UnknownUserException | UnknownFileException exc3) {
      log.warn(exc3.getMessage(), exc3);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404EmptyToString(),
          exc3);
    }
  }

  @Override
  public ResponseEntity<Void> deleteFileSharedWithUser(
      @PathVariable("fileID") String fileID,
      @NotNull @Valid @RequestParam(value = "userID", required = true) String userID) {
    try {
      Authentication authentication = SecurityContextHolder
          .getContext()
          .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);
      fileService.removeShareOnFileOnBehalfOf(fileID, userID, requesterId);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (WrongAuthenticationException exc) {
      log.debug("wrong authentication !");
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          HttpErrorAnswerBuilder.build401EmptyToString(),
          exc);
    } catch (UserUnauthorizedException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
          exc);
    } catch (UnknownUserException exc2) {
      log.warn(exc2.getMessage(), exc2);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404UserNotFoundToString(),
          exc2);
    } catch (UnknownFileException exc3) {
      log.warn(exc3.getMessage(), exc3);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404FileNotFoundToString(),
          exc3);
    }
  }

  @Override
  public ResponseEntity<FileBasics> getFileInfo(
      @PathVariable("fileShortUrl") String fileShortUrl) {
    DBFile dbFile;
    try {
      dbFile = fileService.findAvailableFileByShortUrl(fileShortUrl);
      FileBasics fileInfo = dbFile.toFileBasics();
      HttpHeaders responseHeaders = new HttpHeaders();
      return new ResponseEntity<FileBasics>(
          fileInfo,
          responseHeaders,
          HttpStatus.OK);
    } catch (UnknownFileException e) {
      // TODO Auto-generated catch block
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404EmptyToString(),
          e);
    }
  }

  @ApiIgnore
  @RequestMapping(value = "/file/{fileID}", method = RequestMethod.HEAD)
  public ResponseEntity<String> headFile(
      @PathVariable("fileID") String fileID,
      @RequestParam(value = "password", required = false) String password) {
    HttpHeaders responseHeaders = new HttpHeaders();
    try {
      DownloadReturn downloadReturn = fileService.downloadFile(
          fileID,
          password,
          true);
      ContentDisposition cd = ContentDisposition
          .builder("attachment")
          .filename(downloadReturn.getFilename(), StandardCharsets.UTF_8)
          .build();
      responseHeaders.setContentDisposition(cd);
      responseHeaders.set(
          HttpHeaders.CONTENT_LENGTH,
          downloadReturn.getFileSizeInBytes().toString());
      return new ResponseEntity<String>(null, responseHeaders, HttpStatus.OK);
    } catch (UnknownFileException exc3) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404EmptyToString(),
          exc3);
    } catch (WrongPasswordException exc4) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          HttpErrorAnswerBuilder.build401EmptyToString(),
          exc4);
    }
  }

  @Override
  public ResponseEntity<Resource> getFile(
      @PathVariable("fileID") String fileID,
      @RequestParam(value = "password", required = false) String password) {
    try {
      DownloadReturn downloadReturn = fileService.downloadFile(
          fileID,
          password,
          false);
      File file = downloadReturn.getFile();
      InputStream stream = new FileInputStream(file);
      InputStreamResource inputStreamResource = new InputStreamResource(stream);
      HttpHeaders responseHeaders = new HttpHeaders();
      ContentDisposition cd = ContentDisposition
          .builder("attachment")
          .filename(downloadReturn.getFilename(), StandardCharsets.UTF_8)
          .build();
      responseHeaders.setContentDisposition(cd);
      responseHeaders.set(
          HttpHeaders.CONTENT_LENGTH,
          downloadReturn.getFileSizeInBytes().toString());

      ResponseEntity<Resource> responseEntity = new ResponseEntity<>(
          inputStreamResource,
          responseHeaders,
          HttpStatus.OK);
      return responseEntity;
    } catch (UnknownFileException exc3) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404FileNotFoundToString(),
          exc3);
    } catch (WrongPasswordException exc4) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          HttpErrorAnswerBuilder.build401EmptyToString(),
          exc4);
    } catch (FileNotFoundException exc5) {
      log.error(exc5.getMessage(), exc5);
      // should never occur
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          HttpErrorAnswerBuilder.build500EmptyToString());
    }
  }

  @Override
  public ResponseEntity<FileResult> postFileFileRequest(
      @RequestBody(required = false) FileRequest fileRequest, String X_EU_CAPTCHA_ID,
      String X_EU_CAPTCHA_TOKEN, String X_EU_CAPTCHA_TEXT) {

    if (!FileRequestValidator.validate(fileRequest)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          HttpErrorAnswerBuilder.build400EmptyToString());
    }
    try {
         Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);

      DBUser.Role userRole = userService.getDbUser(requesterId).getRole();
     
      if(userRole.equals(DBUser.Role.EXTERNAL))
        captchaValidator.checkCaptcha(X_EU_CAPTCHA_ID, X_EU_CAPTCHA_TOKEN, X_EU_CAPTCHA_TEXT);
    
      String fileId = fileService.allocateFileOnBehalfOf(
          fileRequest.getExpirationDate(),
          fileRequest.getName(),
          fileRequest.getPassword(),
          requesterId,
          fileRequest.getSharedWith(),
          fileRequest.getSize().longValue(),
          requesterId,
          fileRequest.getDownloadNotification());
      return new ResponseEntity<>(
          (new FileResult()).fileId(fileId),
          HttpStatus.OK);
    } catch (WrongAuthenticationException exc) {
      log.debug("wrong authentication !");
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          HttpErrorAnswerBuilder.build401EmptyToString(),
          exc);
    } catch (IllegalFileSizeException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403IllegalFileSizeToString(),
          exc);
    } catch (DateLiesInPastException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403DateLiesInPastToString(),
          exc);
    } catch (UserHasInsufficientSpaceException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403UserHasInsufficientSpaceToString(),
          exc);
    } catch (UserHasNoUploadRightsException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403UserHasNoUploadRightsToString(),
          exc);
    } catch (UserUnauthorizedException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
          exc);
    } catch (EmptyFilenameException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403EmptyFileNameToString(),
          exc);
    } catch (MessageTooLongException e) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403MessageTooLongToString(),
          e);
    } catch (CouldNotAllocateFileException | UnknownUserException exc3) {
      log.error(exc3.getMessage(), exc3);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          HttpErrorAnswerBuilder.build500EmptyToString());
    }
  }

  @Override
  public ResponseEntity<FileInfoUploader> postFileContent(
      @PathVariable("fileID") String fileID,
      @RequestPart("file") MultipartFile body) {
    if ((body == null) || body.getSize() == 0L) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          HttpErrorAnswerBuilder.build400EmptyToString());
    }

    try {
      Authentication authentication = SecurityContextHolder
          .getContext()
          .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);
      FileInfoUploader fileInfoUploader = fileService.saveOnBehalfOf(
          fileID,
          body,
          requesterId);
      return new ResponseEntity<>(fileInfoUploader, HttpStatus.OK);
    } catch (WrongAuthenticationException exc) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          HttpErrorAnswerBuilder.build401EmptyToString(),
          exc);
    } catch (FileLargerThanAllocationException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403FileLargerThanAllocationToString(),
          exc);
    } catch (IllegalFileSizeException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403IllegalFileSizeToString(),
          exc);
    } catch (UserUnauthorizedException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
          exc);
    } catch (UnknownFileException exc2) {
      log.warn(exc2.getMessage(), exc2);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404EmptyToString(),
          exc2);
    } catch (
        IllegalFileStateException
        | CouldNotSaveFileException
        | MessagingException exc3) {
      log.error(exc3.getMessage(), exc3);
      // should never occur
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          HttpErrorAnswerBuilder.build500EmptyToString());
    }
  }

  @Override
  public ResponseEntity<Recipient> postFileSharedWith(
      @PathVariable("fileID") String fileID,
      @RequestBody Recipient recipient) {
    if (!RecipientValidator.validate(recipient)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          HttpErrorAnswerBuilder.build400EmptyToString());
    }
    try {
      Authentication authentication = SecurityContextHolder
          .getContext()
          .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);
      Recipient recipientWithLink = fileService.addShareOnFileOnBehalfOf(
          fileID,
          recipient,
          requesterId,
          recipient.getDownloadNotification());
      return new ResponseEntity<>(recipientWithLink, HttpStatus.OK);
    } catch (WrongAuthenticationException exc) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          HttpErrorAnswerBuilder.build401EmptyToString(),
          exc);
    } catch (UserUnauthorizedException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
          exc);
    } catch (UnknownFileException exc2) {
      log.warn(exc2.getMessage(), exc2);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404EmptyToString(),
          exc2);
    } catch (UnknownUserException exc3) {
      log.warn(exc3.getMessage(), exc3);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404EmptyToString(),
          exc3);
    } catch (MessageTooLongException e) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403MessageTooLongToString(),
          e);
    } catch (MessagingException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          HttpErrorAnswerBuilder.build500EmptyToString(),
          e);
    } catch (UserHasNoUploadRightsException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403UserHasNoUploadRightsToString(),
          exc);
    }
  }

  @Override
  public ResponseEntity<Void> postFileSharedWithReminder(
      String fileID,
      @NotNull @Valid String userEmail) {
    try {
      Authentication authentication = SecurityContextHolder
          .getContext()
          .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);
      fileService.reminderShareOnFileOnBehalfOf(fileID, userEmail, requesterId);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (WrongAuthenticationException exc) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          HttpErrorAnswerBuilder.build401EmptyToString(),
          exc);
    } catch (UserUnauthorizedException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
          exc);
    } catch (UnknownFileException exc2) {
      log.warn(exc2.getMessage(), exc2);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404EmptyToString(),
          exc2);
    } catch (UnknownUserException exc3) {
      log.warn(exc3.getMessage(), exc3);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404EmptyToString(),
          exc3);
    } catch (MessagingException e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          HttpErrorAnswerBuilder.build500EmptyToString(),
          e);
    }
  }

  @Override
  public ResponseEntity<Void> postFileSharedWithDownloadNotification(String fileID, @NotNull @Valid String userEmail,
      Boolean downloadNotification) {
    try {
      Authentication authentication = SecurityContextHolder
          .getContext()
          .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);
      fileService.changeDownloadNotificationShareOnFileOnBehalfOf(fileID, userEmail, requesterId, downloadNotification);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (WrongAuthenticationException exc) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          HttpErrorAnswerBuilder.build401EmptyToString(),
          exc);
    } catch (UserUnauthorizedException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
          exc);
    } catch (UnknownFileException exc2) {
      log.warn(exc2.getMessage(), exc2);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404EmptyToString(),
          exc2);
    } catch (UnknownUserException exc3) {
      log.warn(exc3.getMessage(), exc3);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404EmptyToString(),
          exc3);
    }
  }

  @Override
  public ResponseEntity<Void> updateFile(
      String fileID,
      @Valid FileBasics fileBasics) {
    try {
      Authentication authentication = SecurityContextHolder
          .getContext()
          .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);

      fileService.updateFileOnBehalfOf(
          fileID,
          fileBasics.getExpirationDate(),
          requesterId);

      return new ResponseEntity<>(HttpStatus.OK);
    } catch (UnknownFileException exc3) {
      log.warn(exc3.getMessage(), exc3);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404EmptyToString(),
          exc3);
    } catch (WrongAuthenticationException exc) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          HttpErrorAnswerBuilder.build401EmptyToString(),
          exc);
    } catch (UserUnauthorizedException exc) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
          exc);
    } catch (UnknownUserException exc3) {
      log.warn(exc3.getMessage(), exc3);
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          HttpErrorAnswerBuilder.build404EmptyToString(),
          exc3);
    }
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<Void> updateStatus(String fileID, @Valid FileStatusUpdate fileStatusUpdate) {
    Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
    String requesterId;
    try {
      requesterId = userService.getAuthenticatedUserId(authentication);

      if (!userService.isAdmin(requesterId))
        throw new WrongAuthenticationException("not Admin user");

      DBFile file = fileRepository.findOneById(fileID);
      if (file != null) {
        DBFile.Status dbfileStatus = EnumConverter.convert(fileStatusUpdate.getStatus(), DBFile.Status.class);
        file.setStatus(dbfileStatus);
        fileRepository.save(file);
      }

    } catch (WrongAuthenticationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (UnknownUserException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return ResponseEntity.noContent().build();

  }

}
