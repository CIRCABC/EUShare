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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.circabc.easyshare.storage.DBUser;
import com.circabc.easyshare.storage.UserRepository;
import com.circabc.easyshare.storage.DBUser.Role;
import com.circabc.easyshare.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

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
        if (dbUser != null && dbUser.getPassword().equals(credentials.getPassword())) {
            return dbUser.getId();
        }
        throw new WrongAuthenticationException();
    }

    public DBUser createInternalUser(String email, String name, String password, String username) {
        DBUser user = DBUser.createInternalUser(email, name, password, esConfig.getDefaultUserSpace(), username);
        return userRepository.save(user);
    }

    public DBUser createAdminUser(String password) {
        DBUser user = this.createInternalUser("admin@admin.admin", "admin", password, "admin"); // NOSONAR
        user.setRole(Role.ADMIN);
        return userRepository.save(user);
    }

    /**
     * One of the arguments can be null
     */
    public DBUser createExternalUser(String email, String name) {
            DBUser user = DBUser.createExternalUser(email, name);
            return userRepository.save(user);
    }

    DBUser getDbUser(String userId) throws UnknownUserException {
        return userRepository.findById(userId).orElseThrow(() -> new UnknownUserException());
    }

    public DBUser getUserOrCreateExternalUser(Recipient recipient) throws WrongEmailStructureException {
        final String emailOrName = recipient.getEmailOrName();
        DBUser dbUser = userRepository.findOneByName(emailOrName);
        if (dbUser == null) {
            dbUser = userRepository.findOneByEmail(emailOrName);
        }
        if (dbUser != null) {
            return dbUser;
        } else {
            if (StringUtils.validateEmailAddress(emailOrName)) {
                return this.createExternalUser(emailOrName, null);
            }
            if (StringUtils.validateUsername(emailOrName)) {
                return this.createExternalUser(null, emailOrName);
            }
            throw new WrongEmailStructureException();
        }
    }

    public List<DBUser> getDbUsersByIds(Iterable<String> ids) {
        return (List<DBUser>) userRepository.findAllById(ids);
    }

    public void createDefaultUsers() {
        if (userRepository.findOneByUsername("admin") == null && userRepository.findOneByEmail("admin@admin.admin") == null) {
            this.createAdminUser("admin");
        } else {
            log.warn("Admin could not be created, already exists");
        }
        if (userRepository.findOneByUsername("username") == null && userRepository.findOneByEmail("email@email.com") == null)  {
            this.createInternalUser("email@email.com", "name", "username", "password");
        } else {
            log.warn("Internal user could not be created, already exists");
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

    public UserInfo setUserInfoOnBehalfOf(UserInfo userInfo, String requesterId) throws UnknownUserException,
            UserUnauthorizedException, ExternalUserCannotBeAdminException, IllegalSpaceException {
        String userId = userInfo.getId();
        if (this.isAdmin(requesterId)) {
            UserInfo oldUserInfo = this.getUserInfo(userId);
            // Id, Name, UsedSpace
            if (!oldUserInfo.getId().equals(userId) || !oldUserInfo.getName().equals(userInfo.getName())
                    || !oldUserInfo.getUsedSpace().equals(userInfo.getUsedSpace())) {
                throw new UserUnauthorizedException();
            }
            // isAdmin
            if (!oldUserInfo.getIsAdmin().equals(userInfo.getIsAdmin())) {
                if (oldUserInfo.getIsAdmin()) {
                    this.revokeAdminRights(userId);
                } else {
                    this.grantAdminRights(userId);
                }
            }
            // TotalSpace
            if (!oldUserInfo.getTotalSpace().equals(userInfo.getTotalSpace())) {
                this.setSpace(userId, userInfo.getTotalSpace().longValue());
            }
            return userInfo;
        } else {
            throw new UserUnauthorizedException();
        }

    }

    public List<UserInfo> getUsersUserInfoOnBehalfOf(int pageSize, int pageNumber, String searchString,
            String requesterId) throws UnknownUserException, UserUnauthorizedException {
        if (isAdmin(requesterId)) {
            return userRepository.findByEmailStartsWith(searchString, PageRequest.of(pageNumber, pageSize)).stream()
                    .map(dbUser -> dbUser.toUserInfo()).collect(Collectors.toList());
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

    boolean isRequesterIdEqualsToUserIdOrIsAnAdmin(String userId, String requesterId) throws UnknownUserException {
        return ((requesterId.equals(userId)) || (this.getDbUser(requesterId).getRole().equals(Role.ADMIN)));
    }

    /**
     * Grant admin rights to the specified user.
     */
    private void grantAdminRights(String userId) throws UnknownUserException, ExternalUserCannotBeAdminException {
        DBUser user = this.getDbUser(userId);

        if (user.getRole().equals(DBUser.Role.EXTERNAL)) {
            throw new ExternalUserCannotBeAdminException();
        }

        user.setRole(DBUser.Role.ADMIN);
        userRepository.save(user);
    }

    public boolean isAdmin(String userId) throws UnknownUserException {
        DBUser dbUser = this.getDbUser(userId);
        return dbUser.getRole().equals(DBUser.Role.ADMIN);
    }

    /**
     * Resets the role of a user to {@code INTERNAL}.
     */
    private void revokeAdminRights(String userId) throws UnknownUserException {
        DBUser user = this.getDbUser(userId);
        if (user.getRole().equals(Role.ADMIN)) {
            user.setRole(DBUser.Role.INTERNAL);
            userRepository.save(user);
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
    private void setSpace(String userId, long space) throws IllegalSpaceException, UnknownUserException {
        if (space < 0) {
            throw new IllegalSpaceException();
        }
        DBUser user = this.getDbUser(userId);
        user.setTotalSpace(space);
        userRepository.save(user);
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
