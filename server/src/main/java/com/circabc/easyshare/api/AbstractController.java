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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import com.circabc.easyshare.exceptions.NoAuthenticationException;
import com.circabc.easyshare.model.Credentials;

import org.springframework.web.context.request.NativeWebRequest;

public abstract class AbstractController {


    /**
     * Returns an array containing the username then the password
     * @param optionalNativeWebRequest
     */
    protected Credentials getAuthenticationUsernameAndPassword(Optional<NativeWebRequest> optionalNativeWebRequest) throws NoAuthenticationException{
        if (optionalNativeWebRequest.isPresent()){
            final String authorizationHeaderValue = optionalNativeWebRequest.get().getHeader("Authorization");
            if (authorizationHeaderValue != null && authorizationHeaderValue.toLowerCase().startsWith("basic")) {
                String base64Credentials = authorizationHeaderValue.substring("BASIC".length()).trim();
                byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                String[] credentialsArray = credentials.split(":", 2);
                Credentials crdt = new Credentials();
                crdt.setEmail(credentialsArray[0]);
                crdt.setPassword(credentialsArray[1]);
                return crdt;
            }
        }
        throw new NoAuthenticationException();
    }


}