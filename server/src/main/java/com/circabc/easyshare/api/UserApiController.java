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

import java.util.List;
import java.util.Optional;

import com.circabc.easyshare.error.HttpErrorAnswerBuilder;
import com.circabc.easyshare.exceptions.NonInternalUsersCannotBecomeAdminException;
import com.circabc.easyshare.exceptions.IllegalSpaceException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.model.FileInfoRecipient;
import com.circabc.easyshare.model.FileInfoUploader;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.model.validation.UserInfoValidator;
import com.circabc.easyshare.services.FileService;
import com.circabc.easyshare.services.UserService;

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

import lombok.extern.slf4j.Slf4j;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-09-30T14:41:19.080+02:00[Europe/Paris]")

@Slf4j
@Controller
@RequestMapping("${openapi.easyShare.base-path:}")
public class UserApiController implements UserApi {

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
    public ResponseEntity<List<FileInfoRecipient>> getFilesFileInfoRecipient(@PathVariable("userID") String userID,
            @RequestParam(value = "pageSize", required = true) Integer pageSize,
            @RequestParam(value = "pageNumber", required = true) Integer pageNumber) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String requesterId = userService.getAuthenticatedUserId(authentication);
            List<FileInfoRecipient> fileInfoRecipientList = fileService.getFileInfoRecipientOnBehalfOf(pageSize,
                    pageNumber, userID, requesterId);
            return new ResponseEntity<>(fileInfoRecipientList, HttpStatus.OK);
        } catch (WrongAuthenticationException exc) {
            log.debug("wrong authentication !");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, HttpErrorAnswerBuilder.build401EmptyToString(),
                    exc);
        } catch (UserUnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), e);
        } catch (UnknownUserException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), e);
        }
    }

    @Override
    public ResponseEntity<List<FileInfoUploader>> getFilesFileInfoUploader(@PathVariable("userID") String userID,
            @RequestParam(value = "pageSize", required = true) Integer pageSize,
            @RequestParam(value = "pageNumber", required = true) Integer pageNumber) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String requesterId = userService.getAuthenticatedUserId(authentication);
            List<FileInfoUploader> fileInfoUploaderList = fileService.getFileInfoUploaderOnBehalfOf(pageSize,
                    pageNumber, userID, requesterId);
            return new ResponseEntity<>(fileInfoUploaderList, HttpStatus.OK);
        } catch (WrongAuthenticationException exc) {
            log.debug("wrong authentication !");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, HttpErrorAnswerBuilder.build401EmptyToString(),
                    exc);
        } catch (UserUnauthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), e);
        } catch (UnknownUserException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), e);
        }
    }

    @Override
    public ResponseEntity<UserInfo> putUserUserInfo(@PathVariable("userID") String userID,
            @RequestBody UserInfo userInfo) {
        if (!UserInfoValidator.validate(userInfo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HttpErrorAnswerBuilder.build400EmptyToString());
        }
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String requesterId = userService.getAuthenticatedUserId(authentication);
            UserInfo acceptedUserInfo = userService.setUserInfoOnBehalfOf(userInfo, requesterId);
            return new ResponseEntity<>(acceptedUserInfo, HttpStatus.OK);
        } catch (WrongAuthenticationException exc) {
            log.debug("wrong authentication !");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, HttpErrorAnswerBuilder.build401EmptyToString(),
                    exc);
        } catch (UserUnauthorizedException | NonInternalUsersCannotBecomeAdminException | IllegalSpaceException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), e);
        } catch (UnknownUserException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, HttpErrorAnswerBuilder.build404EmptyToString(), e);
        }
    }

    @Override
    public ResponseEntity<UserInfo> getUserUserInfo(@PathVariable("userID") String userID) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String requesterId = userService.getAuthenticatedUserId(authentication);
            UserInfo userInfo = userService.getUserInfoOnBehalfOf(userID, requesterId);
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } catch (WrongAuthenticationException exc2) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, HttpErrorAnswerBuilder.build401EmptyToString(),
                    exc2);
        } catch (UserUnauthorizedException ec) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), ec);
        } catch (UnknownUserException exc3) {
            log.error("Search on non existing userId, might be a security attack");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString());
        }
    }

}
