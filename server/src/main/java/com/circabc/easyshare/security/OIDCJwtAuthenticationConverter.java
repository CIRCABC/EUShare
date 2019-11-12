/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package com.circabc.easyshare.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import com.circabc.easyshare.services.UserService;

/** JWT converter that takes the roles from 'groups' claim of JWT token. */
@SuppressWarnings("unused")
public class OIDCJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String GROUPS_CLAIM = "groups";
    private static final String ROLE_PREFIX = "ROLE_";

    private final UserService userDetailsService;

    public OIDCJwtAuthenticationConverter(UserService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        UserDetails userDetails = userDetailsService.getOrCreateUserDetails(jwt.getClaimAsString("email"), jwt.getClaimAsString("given_name"));
        AbstractAuthenticationToken abstractAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, "n/a", userDetails.getAuthorities());
        return abstractAuthenticationToken;
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        return this.getGroups(jwt).stream().map(authority -> ROLE_PREFIX + authority.toUpperCase())
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getGroups(Jwt jwt) {
        Object groups = jwt.getClaims().get(GROUPS_CLAIM);
        if (groups instanceof Collection) {
            return (Collection<String>) groups;
        }

        return Collections.emptyList();
    }
}