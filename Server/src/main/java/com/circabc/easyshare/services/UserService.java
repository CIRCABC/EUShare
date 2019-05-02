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

import com.circabc.easyshare.configuration.EasyShareConfiguration;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.exceptions.WrongEmailStructureException;
import com.circabc.easyshare.exceptions.ExternalUserCannotBeAdminException;
import com.circabc.easyshare.exceptions.IllegalSpaceException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.model.Credentials;
import com.circabc.easyshare.model.Recipient;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.model.UserSpace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.circabc.easyshare.storage.DBUser;
import com.circabc.easyshare.storage.UserRepository;
import com.circabc.easyshare.storage.DBUser.Role;
import com.circabc.easyshare.utils.StringUtils;

import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EasyShareConfiguration esConfig;

    private UserService() {
    }

    public String getAuthenticatedUserId(Credentials credentials) throws WrongAuthenticationException {
        DBUser dbUser = userRepository.findOneByUsername(credentials.getEmail());
        if (dbUser != null) {
            if (dbUser.getPassword().equals(credentials.getPassword())) {
                return dbUser.getId();
            }
        }
        throw new WrongAuthenticationException();
    }


    public DBUser createInternalUser(String userId, String username, String password) {
        DBUser user = DBUser.createInternalUser(userId, username, password, esConfig.getDefaultUserSpace());
        return userRepository.saveAndFlush(user);
    }

    public DBUser createInternalUser(String username, String password) {
        String userId = StringUtils.randomString();
        DBUser user = DBUser.createInternalUser(userId, username, password, esConfig.getDefaultUserSpace());
        return userRepository.saveAndFlush(user);
    }

    public DBUser createAdminUser(String password) {
        DBUser user = this.createInternalUser("admin", password);
        user.setRole(Role.ADMIN);
        return userRepository.saveAndFlush(user);
    }

    public DBUser createExternalUser(String email) throws WrongEmailStructureException {
        if(StringUtils.validateEmailAddress(email)) {
            DBUser user = DBUser.createExternalUser(email);
            return userRepository.saveAndFlush(user);
        } else {
            throw new WrongEmailStructureException();
        }
    }

    public DBUser getDbUser(String userId) throws UnknownUserException {
        return userRepository.findById(userId).orElseThrow(() -> new UnknownUserException());
    }

    public DBUser getUserOrCreateExternalUser(Recipient recipient) throws WrongEmailStructureException {
        final String emailOrId = recipient.getEmailOrID();
        final DBUser dbUser = userRepository.findById(emailOrId).orElse(null);
        if (dbUser != null) {
            return dbUser;
        } else {
           return createExternalUser(emailOrId);
        }
    }

    public List<DBUser> getDbUsersByIds(Iterable<String> ids) {
        return userRepository.findAllById(ids);
    }

    public void createDefaultUsers() {
        if (userRepository.findOneByUsername("admin") == null) {
            this.createAdminUser("admin");
        }
        if (userRepository.findOneByUsername("username") == null) {
            this.createInternalUser("username", "password");
        }
    }

    /**
     * Returns the {@code userId}'s Role if the corresponding user exists.
     */
    public DBUser.Role getRole(String userId) throws UnknownUserException {
        return this.getDbUser(userId).getRole();
    }

    /**
     * Returns the {@code userId}'s UserInfo if the corresponding user exists.
     */
    public UserInfo getUserInfo(String userId) throws UnknownUserException {
        return this.getDbUser(userId).toUserInfo();
    }

    /**
     * Returns the {@code userId}'s UserInfo if the corresponding user exists.
     */
    public UserSpace getUserSpace(String userId) throws UnknownUserException {
        return this.getDbUser(userId).toUserSpace();
    }

    public UserInfo getUserInfoOnBehalfOf(String userId, String requesterId)
            throws UnknownUserException, UserUnauthorizedException {
        if (isRequesterIdEqualsToUserIdOrIsAnAdmin(userId, requesterId)) {
            return getUserInfo(userId);
        } else {
            throw new UserUnauthorizedException();
        }
    }

    public UserSpace getUserSpaceOnBehalfOf(String userId, String requesterId)
            throws UserUnauthorizedException, UnknownUserException {
        if (isRequesterIdEqualsToUserIdOrIsAnAdmin(userId, requesterId)) {
            return getUserSpace(userId);
        } else {
            throw new UserUnauthorizedException();
        }
    }

    public void grantAdminRightsOnBehalfOf(String userId, String requesterId)
            throws UnknownUserException, ExternalUserCannotBeAdminException, UserUnauthorizedException {
        if (isAdmin(requesterId)) {
            grantAdminRights(userId);
        } else {
            throw new UserUnauthorizedException();
        }
    }

    boolean isRequesterIdEqualsToUserIdOrIsAnAdmin(String userId, String requesterId)
            throws UnknownUserException {
        return ((requesterId.equals(userId)) || (this.getDbUser(requesterId).getRole().equals(Role.ADMIN)));
    }

    /**
     * Grant admin rights to the specified user.
     */
    public void grantAdminRights(String userId) throws UnknownUserException, ExternalUserCannotBeAdminException {
        DBUser user = this.getDbUser(userId);

        if (user.getRole().equals(DBUser.Role.EXTERNAL)) {
            throw new ExternalUserCannotBeAdminException();
        }

        user.setRole(DBUser.Role.ADMIN);
        userRepository.saveAndFlush(user);
    }

    public boolean isAdmin(String userId) throws UnknownUserException {
        DBUser dbUser = this.getDbUser(userId);
        return dbUser.getRole().equals(DBUser.Role.ADMIN);
    }

    /**
     * Resets the role of a user to {@code INTERNAL}.
     */
    public void revokeAdminRights(String userId) throws UnknownUserException {
        DBUser user = this.getDbUser(userId);
        if (user.getRole().equals(Role.ADMIN)) {
            user.setRole(DBUser.Role.INTERNAL);
            userRepository.saveAndFlush(user);
        }
    }

    public void revokeAdminRightsOnBehalfOf(String userId, String requesterId)
            throws UnknownUserException, UserUnauthorizedException {
        if (this.isAdmin(requesterId)) {
            this.revokeAdminRights(userId);
        } else {
            throw new UserUnauthorizedException();
        }
    }

    /**
     * Sets {@code userId}'s maximum space to {@code space}
     */
    public void setSpace(String userId, long space) throws IllegalSpaceException, UnknownUserException {
        if (space < 0) {
            throw new IllegalSpaceException();
        }
        DBUser user = this.getDbUser(userId);
        user.setTotalSpace(space);
        userRepository.saveAndFlush(user);
    }

    public void setSpaceOnBehalfOf(String userId, long space, String requesterId)
            throws UnknownUserException, IllegalSpaceException, UserUnauthorizedException {
        if (isRequesterIdEqualsToUserIdOrIsAnAdmin(userId, requesterId)) {
            this.setSpace(userId, space);
        } else {
            throw new UserUnauthorizedException();
        }
    }
}
