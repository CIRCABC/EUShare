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

import com.circabc.easyshare.exceptions.ExternalUserCannotBeAdminException;
import com.circabc.easyshare.exceptions.IllegalSpaceException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.model.UserSpace;

public interface UserServiceInterface {

    public UserInfo getUserInfoOnBehalfOf(String userId, String requesterId)
            throws UnknownUserException, UserUnauthorizedException;

    public UserInfo setUserInfoOnBehalfOf(UserInfo userInfo, String requesterId) throws UnknownUserException,
            UserUnauthorizedException, ExternalUserCannotBeAdminException, IllegalSpaceException;

    public List<UserInfo> getUsersUserInfoOnBehalfOf(int pageSize, int pageNumber, String searchString,
            String requesterId) throws UnknownUserException, UserUnauthorizedException;

    public UserSpace getUserSpaceOnBehalfOf(String userId, String requesterId)
            throws UserUnauthorizedException, UnknownUserException;

    public void grantAdminRightsOnBehalfOf(String userId, String requesterId)
            throws UnknownUserException, ExternalUserCannotBeAdminException, UserUnauthorizedException;

    public void revokeAdminRightsOnBehalfOf(String userId, String requesterId)
            throws UnknownUserException, UserUnauthorizedException;

    public void setSpaceOnBehalfOf(String userId, long space, String requesterId)
            throws UnknownUserException, IllegalSpaceException, UserUnauthorizedException;

}