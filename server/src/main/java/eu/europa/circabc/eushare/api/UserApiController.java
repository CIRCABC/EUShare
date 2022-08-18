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
import eu.europa.circabc.eushare.exceptions.IllegalSpaceException;
import eu.europa.circabc.eushare.exceptions.NonInternalUsersCannotBecomeAdminException;
import eu.europa.circabc.eushare.exceptions.UnknownUserException;
import eu.europa.circabc.eushare.exceptions.UserUnauthorizedException;
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.model.FileInfoRecipient;
import eu.europa.circabc.eushare.model.FileInfoUploader;
import eu.europa.circabc.eushare.model.UserInfo;
import eu.europa.circabc.eushare.model.validation.UserInfoValidator;
import eu.europa.circabc.eushare.services.FileService;
import eu.europa.circabc.eushare.services.UserService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.server.ResponseStatusException;

@javax.annotation.Generated(
  value = "org.openapitools.codegen.languages.SpringCodegen"
)
@Controller
@RequestMapping("${openapi.easyShare.base-path:}")
public class UserApiController implements UserApi {

  private Logger log = LoggerFactory.getLogger(UserApiController.class);

  private final NativeWebRequest request;

  @Autowired
  private UserService userService;

  @Autowired
  private FileService fileService;

  @org.springframework.beans.factory.annotation.Autowired
  public UserApiController(NativeWebRequest request) {
    this.request = request;
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<List<FileInfoRecipient>> getFilesFileInfoRecipient(
    @PathVariable("userID") String userID,
    @RequestParam(value = "pageSize", required = true) Integer pageSize,
    @RequestParam(value = "pageNumber", required = true) Integer pageNumber
  ) {
    try {
      Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);
      List<FileInfoRecipient> fileInfoRecipientList = fileService.getFileInfoRecipientOnBehalfOf(
        pageSize,
        pageNumber,
        userID,
        requesterId
      );
      return new ResponseEntity<>(fileInfoRecipientList, HttpStatus.OK);
    } catch (WrongAuthenticationException exc) {
      log.debug("wrong authentication !");
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        HttpErrorAnswerBuilder.build401EmptyToString(),
        exc
      );
    } catch (UserUnauthorizedException e) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
        e
      );
    } catch (UnknownUserException e) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        HttpErrorAnswerBuilder.build404EmptyToString(),
        e
      );
    }
  }

  @Override
  public ResponseEntity<List<FileInfoUploader>> getFilesFileInfoUploader(
    @PathVariable("userID") String userID,
    @RequestParam(value = "pageSize", required = true) Integer pageSize,
    @RequestParam(value = "pageNumber", required = true) Integer pageNumber
  ) {
    try {
      Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);
      List<FileInfoUploader> fileInfoUploaderList = fileService.getFileInfoUploaderOnBehalfOf(
        pageSize,
        pageNumber,
        userID,
        requesterId
      );
      return new ResponseEntity<>(fileInfoUploaderList, HttpStatus.OK);
    } catch (WrongAuthenticationException exc) {
      log.debug("wrong authentication !");
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        HttpErrorAnswerBuilder.build401EmptyToString(),
        exc
      );
    } catch (UserUnauthorizedException e) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
        e
      );
    } catch (UnknownUserException e) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        HttpErrorAnswerBuilder.build404EmptyToString(),
        e
      );
    }
  }

  @Override
  public ResponseEntity<UserInfo> putUserUserInfo(
    @PathVariable("userID") String userID,
    @RequestBody UserInfo userInfo
  ) {
    if (!UserInfoValidator.validate(userInfo)) {
      throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        HttpErrorAnswerBuilder.build400EmptyToString()
      );
    }
    try {
      Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);
      UserInfo acceptedUserInfo = userService.setUserInfoOnBehalfOf(
        userInfo,
        requesterId
      );
      return new ResponseEntity<>(acceptedUserInfo, HttpStatus.OK);
    } catch (WrongAuthenticationException exc) {
      log.debug("wrong authentication !");
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        HttpErrorAnswerBuilder.build401EmptyToString(),
        exc
      );
    } catch (
      UserUnauthorizedException
      | NonInternalUsersCannotBecomeAdminException
      | IllegalSpaceException e
    ) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
        e
      );
    } catch (UnknownUserException e) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        HttpErrorAnswerBuilder.build404EmptyToString(),
        e
      );
    }
  }

  @Override
  public ResponseEntity<UserInfo> getUserUserInfo(
    @PathVariable("userID") String userID
  ) {
    try {
      Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);
      UserInfo userInfo = userService.getUserInfoOnBehalfOf(
        userID,
        requesterId
      );
      return new ResponseEntity<>(userInfo, HttpStatus.OK);
    } catch (WrongAuthenticationException exc2) {
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        HttpErrorAnswerBuilder.build401EmptyToString(),
        exc2
      );
    } catch (UserUnauthorizedException ec) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
        ec
      );
    } catch (UnknownUserException exc3) {
      log.error("Search on non existing userId, might be a security attack");
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        HttpErrorAnswerBuilder.build404EmptyToString()
      );
    }
  }
}
