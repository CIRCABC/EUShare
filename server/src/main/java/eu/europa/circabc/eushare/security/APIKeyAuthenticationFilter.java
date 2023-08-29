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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import eu.europa.circabc.eushare.storage.repository.UserRepository;

public class APIKeyAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    private static String principalRequestHeader = "X-API-KEY";

    public APIKeyAuthenticationFilter(UserRepository userRepository) {

        setAuthenticationManager(new APIKeyAuthenticationManager(userRepository));
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(principalRequestHeader);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            Authentication authResult) {
        if (authResult != null) {
            List<GrantedAuthority> authorities = new ArrayList<>(authResult.getAuthorities());
            authorities.add(new SimpleGrantedAuthority("ROLE_API-KEY"));
            Authentication newAuth = new UsernamePasswordAuthenticationToken(authResult.getPrincipal(),
                    authResult.getCredentials(), authorities);
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }

}
