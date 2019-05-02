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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import com.circabc.easyshare.error.HttpErrorAnswerBuilder;
import com.circabc.easyshare.exceptions.NoAuthenticationException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.model.Credentials;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.services.UserService;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-04-10T14:56:31.271+02:00[Europe/Paris]")

@Slf4j
@Controller
@RequestMapping("${openapi.easyShare.base-path:}")
public class MeApiController extends AbstractController implements MeApi {

    @Autowired
    private UserService userService;

    private final NativeWebRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public MeApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public ResponseEntity<UserInfo> getUserInfo() {
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String userId = userService.getAuthenticatedUserId(credentials);
            UserInfo userInfo = userService.getUserInfoOnBehalfOf(userId, userId);
            return new ResponseEntity<UserInfo>(userInfo, HttpStatus.OK);
        } catch (NoAuthenticationException | WrongAuthenticationException exc2) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.FORBIDDEN,
            HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc2);
            throw responseStatusException;
        } catch (UserUnauthorizedException | UnknownUserException exc3) {
            log.error(exc3.getMessage(),exc3);
            // should never occur
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            HttpErrorAnswerBuilder.build500EmptyToString());
            throw responseStatusException;
        }
    }


    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}

