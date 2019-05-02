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

import com.circabc.easyshare.api.LoginApi;
import com.circabc.easyshare.error.HttpErrorAnswerBuilder;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.model.Credentials;
import com.circabc.easyshare.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-04-10T14:56:31.271+02:00[Europe/Paris]")

@Slf4j
@Controller
@RequestMapping("${openapi.easyShare.base-path:}")
public class LoginApiController implements LoginApi {

    private final NativeWebRequest request;

    @Autowired
    private UserService userService;

    @org.springframework.beans.factory.annotation.Autowired
    public LoginApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<String> postLogin(@RequestBody(required = false) Credentials credentials) {
        log.info("recieved login request");
        if (credentials == null) {
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.BAD_REQUEST,
            HttpErrorAnswerBuilder.build400EmptyToString());
            throw responseStatusException;
        }
        try {
            return new ResponseEntity<String>(userService.getAuthenticatedUserId(credentials), HttpStatus.OK);
        } catch (WrongAuthenticationException exc) {
            log.warn("wrong authentication !");
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.UNAUTHORIZED,
            HttpErrorAnswerBuilder.build401EmptyToString(), exc);
            throw responseStatusException;
        }
    }

}
