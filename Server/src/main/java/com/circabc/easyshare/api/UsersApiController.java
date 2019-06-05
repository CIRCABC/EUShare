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
import com.circabc.easyshare.exceptions.NoAuthenticationException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.model.Credentials;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-04-10T14:56:31.271+02:00[Europe/Paris]")

@Controller
@RequestMapping("${openapi.easyShare.base-path:}")
public class UsersApiController extends AbstractController implements UsersApi {

    private final NativeWebRequest request;

    @Autowired
    private UserService userService;

    @org.springframework.beans.factory.annotation.Autowired
    public UsersApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public ResponseEntity<List<UserInfo>> getUsersUserInfo(
            @RequestParam(value = "pageSize") Integer pageSize,
            @RequestParam(value = "pageNumber") Integer pageNumber,
            @RequestParam(value = "searchString") String searchString) {
        try {
            Credentials credentials = this.getAuthenticationUsernameAndPassword(this.getRequest());
            String requesterId = userService.getAuthenticatedUserId(credentials);
            return new ResponseEntity<>(
                    userService.getUsersUserInfoOnBehalfOf(pageSize, pageNumber, searchString, requesterId),
                    HttpStatus.OK);
        } catch (NoAuthenticationException | WrongAuthenticationException exc) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
            HttpErrorAnswerBuilder.build401EmptyToString(), exc);
        } catch (UserUnauthorizedException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    HttpErrorAnswerBuilder.build403NotAuthorizedToString(), exc);
        } catch (UnknownUserException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
            HttpErrorAnswerBuilder.build500EmptyToString(), e);
        }
    }
       

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}
