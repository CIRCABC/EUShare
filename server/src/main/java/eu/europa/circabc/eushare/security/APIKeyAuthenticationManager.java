/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import eu.europa.circabc.eushare.api.ApiKeyApiController;
import eu.europa.circabc.eushare.storage.entity.DBUser;
import eu.europa.circabc.eushare.storage.repository.UserRepository;

public class APIKeyAuthenticationManager implements AuthenticationManager {

    UserRepository userRepository;

    public APIKeyAuthenticationManager(UserRepository userRepository) {
        this.userRepository= userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
      if (authentication != null &&
          authentication.isAuthenticated() &&
          (authentication instanceof BearerTokenAuthentication) &&
          (authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal)) {
        return authentication;
      }

      if (authentication != null) {
        String apiKey = authentication.getPrincipal().toString();
        String encodedApiKey = ApiKeyApiController.hashApiKey(apiKey);
        DBUser dbUser = null;

        dbUser = userRepository.findOneByApiKey(encodedApiKey);
        if (dbUser != null) {
          authentication.setAuthenticated(true);
          return authentication;
        } else
          throw new BadCredentialsException("API-KEY is not valid");
      } else {
        throw new BadCredentialsException("API-KEY is not valid");
      }

    }
    
}
