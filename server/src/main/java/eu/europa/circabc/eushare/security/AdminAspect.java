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


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import eu.europa.circabc.eushare.exceptions.UnknownUserException;
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.services.UserService;

@Aspect
@Component
public class AdminAspect {

    @Autowired
    private UserService userService;

    @Before("@annotation(eu.europa.circabc.eushare.security.AdminOnly)")
    public void ensureAdminAccess() throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String requesterId;
        try {
            requesterId = userService.getAuthenticatedUserId(authentication);
            if (!userService.isAdmin(requesterId)) {
                throw new WrongAuthenticationException("not Admin user");
            }
        } catch (WrongAuthenticationException | UnknownUserException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access restricted to admins only");
        }
    }
}
