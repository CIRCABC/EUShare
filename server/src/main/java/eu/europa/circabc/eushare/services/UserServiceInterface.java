/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.services;

import eu.europa.circabc.eushare.exceptions.IllegalSpaceException;
import eu.europa.circabc.eushare.exceptions.NonInternalUsersCannotBecomeAdminException;
import eu.europa.circabc.eushare.exceptions.UnknownUserException;
import eu.europa.circabc.eushare.exceptions.UserUnauthorizedException;
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.model.UserInfo;
import java.util.List;
import org.springframework.security.core.Authentication;

public interface UserServiceInterface {
  public String getAuthenticatedUserId(Authentication authentication)
    throws WrongAuthenticationException;

  public UserInfo getUserInfoOnBehalfOf(String userId, String requesterId)
    throws UnknownUserException, UserUnauthorizedException;

  public UserInfo setUserInfoOnBehalfOf(UserInfo userInfo, String requesterId)
    throws UnknownUserException, UserUnauthorizedException, NonInternalUsersCannotBecomeAdminException, IllegalSpaceException;

  public List<UserInfo> getUsersUserInfoOnBehalfOf(
    int pageSize,
    int pageNumber,
    String searchString,
    Boolean active,
    String sortBy,
    String requesterId
  ) throws UnknownUserException, UserUnauthorizedException;

  public void grantAdminRightsOnBehalfOf(String userId, String requesterId)
    throws UnknownUserException, NonInternalUsersCannotBecomeAdminException, UserUnauthorizedException;

  public void revokeAdminRightsOnBehalfOf(String userId, String requesterId)
    throws UnknownUserException, UserUnauthorizedException;

  public void setSpaceOnBehalfOf(String userId, long space, String requesterId)
    throws UnknownUserException, IllegalSpaceException, UserUnauthorizedException;

  public void setAdminUsers();
}
