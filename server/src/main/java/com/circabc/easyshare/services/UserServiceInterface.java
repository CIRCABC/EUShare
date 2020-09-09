/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.services;

import java.util.List;

import com.circabc.easyshare.exceptions.NonInternalUsersCannotBecomeAdminException;
import com.circabc.easyshare.exceptions.IllegalSpaceException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.model.UserInfo;

import org.springframework.security.core.Authentication;

public interface UserServiceInterface {

    public String getAuthenticatedUserId(Authentication authentication) throws WrongAuthenticationException;

    public UserInfo getUserInfoOnBehalfOf(String userId, String requesterId)
            throws UnknownUserException, UserUnauthorizedException;

    public UserInfo setUserInfoOnBehalfOf(UserInfo userInfo, String requesterId) throws UnknownUserException,
            UserUnauthorizedException, NonInternalUsersCannotBecomeAdminException, IllegalSpaceException;

    public List<UserInfo> getUsersUserInfoOnBehalfOf(int pageSize, int pageNumber, String searchString,
            String requesterId) throws UnknownUserException, UserUnauthorizedException;

    public void grantAdminRightsOnBehalfOf(String userId, String requesterId)
            throws UnknownUserException, NonInternalUsersCannotBecomeAdminException, UserUnauthorizedException;

    public void revokeAdminRightsOnBehalfOf(String userId, String requesterId)
            throws UnknownUserException, UserUnauthorizedException;

    public void setSpaceOnBehalfOf(String userId, long space, String requesterId)
            throws UnknownUserException, IllegalSpaceException, UserUnauthorizedException;

    public void createDefaultUsers();

}