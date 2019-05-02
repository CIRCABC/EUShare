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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Optional;

import com.circabc.easyshare.error.HttpErrorAnswerBuilder;
import com.circabc.easyshare.exceptions.ExternalUserCannotBeAdminException;
import com.circabc.easyshare.exceptions.IllegalSpaceException;
import com.circabc.easyshare.exceptions.NoAuthenticationException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.model.Credentials;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.model.UserSpace;
import com.circabc.easyshare.services.UserService;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-04-10T14:56:31.271+02:00[Europe/Paris]")

@Slf4j
@Controller
@RequestMapping("${openapi.easyShare.base-path:}")
public class UserApiController extends AbstractController implements UserApi {

    private final NativeWebRequest request;

    @Autowired
    private UserService userService;

    @org.springframework.beans.factory.annotation.Autowired
    public UserApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<UserInfo> getUserUserInfo(@PathVariable("userID") String userID) {
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            return new ResponseEntity<UserInfo>(userService.getUserInfoOnBehalfOf(userID, requesterId), HttpStatus.OK);
        } catch (UserUnauthorizedException | NoAuthenticationException | WrongAuthenticationException exc) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
            throw responseStatusException;
        } catch (UnknownUserException exc2) {
            log.warn(exc2.getMessage(), exc2);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc2);
            throw responseStatusException;
        }
    }

    @Override
    public ResponseEntity<UserSpace> getUserUserSpace(@PathVariable("userID") String userID) {
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            return new ResponseEntity<UserSpace>(userService.getUserSpaceOnBehalfOf(userID, requesterId),
                    HttpStatus.OK);
        } catch (UserUnauthorizedException | NoAuthenticationException | WrongAuthenticationException exc) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
            throw responseStatusException;
        } catch (UnknownUserException exc2) {
            log.warn(exc2.getMessage(), exc2);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc2);
            throw responseStatusException;
        }
    }

    @Override
    public ResponseEntity<Void> putUserIsAdmin(@PathVariable("userID") String userID,
            @RequestBody(required = false) String body) {
        if (body == null) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    HttpErrorAnswerBuilder.build400EmptyToString());
            throw responseStatusException;
        }
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            boolean bodyIsTrue = Boolean.parseBoolean(body);
            if (bodyIsTrue) {
                userService.grantAdminRightsOnBehalfOf(userID, requesterId);
            } else {
                userService.revokeAdminRightsOnBehalfOf(userID, requesterId);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserUnauthorizedException | NoAuthenticationException | WrongAuthenticationException exc) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
            throw responseStatusException;
        } catch (ExternalUserCannotBeAdminException | UnknownUserException exc2) {
            log.warn(exc2.getMessage(), exc2);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc2);
            throw responseStatusException;
        }
    }

    @Override
    public ResponseEntity<Void> putUserTotalSpace(@PathVariable("userID") String userID,
            @RequestBody(required = false) String body) {
        if (body == null) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    HttpErrorAnswerBuilder.build400EmptyToString());
            throw responseStatusException;
        }
        try {
            long space = Long.parseLong(body);
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String userId = userService.getAuthenticatedUserId(credentials);
            userService.setSpaceOnBehalfOf(userId, space, userID);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (NumberFormatException exc0) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    HttpErrorAnswerBuilder.build400EmptyToString(), exc0);
            throw responseStatusException;
        } catch (NoAuthenticationException | WrongAuthenticationException | IllegalSpaceException | UserUnauthorizedException exc2) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc2);
            throw responseStatusException;
        } catch (UnknownUserException exc3) {
            log.warn(exc3.getMessage(), exc3);
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(), exc3);
            throw responseStatusException;
        }
    }
}
