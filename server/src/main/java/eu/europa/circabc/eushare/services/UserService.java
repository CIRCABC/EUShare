/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package eu.europa.circabc.eushare.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;

import eu.europa.circabc.eushare.configuration.EasyShareConfiguration;
import eu.europa.circabc.eushare.exceptions.IllegalSpaceException;
import eu.europa.circabc.eushare.exceptions.NonInternalUsersCannotBecomeAdminException;
import eu.europa.circabc.eushare.exceptions.UnknownUserException;
import eu.europa.circabc.eushare.exceptions.UserUnauthorizedException;
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.exceptions.WrongEmailStructureException;
import eu.europa.circabc.eushare.exceptions.WrongNameStructureException;
import eu.europa.circabc.eushare.model.Recipient;
import eu.europa.circabc.eushare.model.UserInfo;
import eu.europa.circabc.eushare.storage.DBUser;
import eu.europa.circabc.eushare.storage.UserRepository;
import eu.europa.circabc.eushare.storage.DBUser.Role;
import eu.europa.circabc.eushare.utils.StringUtils;

@Service
public class UserService implements UserServiceInterface, UserDetailsService {

    private Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EasyShareConfiguration esConfig;

    @Value("${spring.security.adminusers}")
    private String[] adminUsers;

    @Override
    public String getAuthenticatedUserId(Authentication authentication) throws WrongAuthenticationException {
        if (authentication != null && authentication.isAuthenticated()
                && (authentication instanceof BearerTokenAuthentication)
                && (authentication.getPrincipal() instanceof DefaultOAuth2AuthenticatedPrincipal)) {
            BearerTokenAuthentication bearerTokenAuthentication = (BearerTokenAuthentication) authentication;
            DefaultOAuth2AuthenticatedPrincipal principal = (DefaultOAuth2AuthenticatedPrincipal) bearerTokenAuthentication
                    .getPrincipal();
            String email = principal.getAttribute("email");
            String givenName = principal.getAttribute("name");
            String username = principal.getAttribute("username");

            if (email == null || username == null) {
                throw new WrongAuthenticationException("Wrong token, cannot find email or username claim");
            }

            DBUser dbUser = null;
            try {
                dbUser = this.getOrCreateInternalUser(email, givenName, username);
            } catch (WrongEmailStructureException e) {
                throw new WrongAuthenticationException(e);
            }
            if (dbUser != null) {
                return dbUser.getId();
            }
        }
        throw new WrongAuthenticationException();
    }

    private DBUser createInternalUser(String email, String givenName, String username) {
        DBUser user = DBUser.createInternalUser(email, givenName, esConfig.getDefaultUserSpace(), username);
        for (String admin : adminUsers) {
            if(admin.equals(username))
               user.setRole(DBUser.Role.ADMIN);
        }
        return userRepository.save(user);
    }


    /**
     * One of the arguments can be null
     */
    private DBUser createExternalUser(String email, String name) {
        DBUser user = DBUser.createExternalUser(email, name);
        return userRepository.save(user);
    }

    private DBUser createLinkUser(String name) {
        DBUser user = DBUser.createLinkUser(name);
        return userRepository.save(user);
    }

    DBUser getDbUser(String userId) throws UnknownUserException {
        return userRepository.findById(userId).orElseThrow(() -> new UnknownUserException());
    }

    DBUser getUserOrCreateExternalOrLinkUser(Recipient recipient)
            throws WrongEmailStructureException, WrongNameStructureException {
        final String emailOrName = recipient.getEmailOrName();
        DBUser dbUser = userRepository.findOneByNameAndRole(emailOrName, DBUser.Role.LINK);
        if (dbUser == null) {
            dbUser = userRepository.findOneByEmailIgnoreCase(emailOrName);
        }
        if (dbUser != null) {
            return dbUser;
        } else {
            if (recipient.getSendEmail()) {
                if (StringUtils.validateEmailAddress(emailOrName)) {
                    return this.createExternalUser(emailOrName, null);
                } else {
                    throw new WrongEmailStructureException();
                }
            } else {
                if (StringUtils.validateLinkName(emailOrName)) {
                    return this.createLinkUser(emailOrName);
                } else {
                    throw new WrongNameStructureException();
                }
            }
        }
    }



    @Override
    public void setAdminUsers() {
        for (String admin : adminUsers) {
            DBUser user = userRepository.findOneByUsername(admin);
            if (user != null) {
                user.setRole(DBUser.Role.ADMIN);
                userRepository.save(user);
                log.warn("User " + admin + " set to admin");
            }
        }        

    }

    /**
     * Returns the {@code userId}'s UserInfo if the corresponding user exists.
     */
    private UserInfo getUserInfo(String userId) throws UnknownUserException {
        return this.getDbUser(userId).toUserInfo();
    }

    @Override
    @Transactional
    public UserInfo getUserInfoOnBehalfOf(String userId, String requesterId)
            throws UnknownUserException, UserUnauthorizedException {
        if (isRequesterIdEqualsToUserIdOrIsAnAdmin(userId, requesterId)) {
            return getUserInfo(userId);
        } else {
            throw new UserUnauthorizedException();
        }
    }

    @Override
    @Transactional
    public UserInfo setUserInfoOnBehalfOf(UserInfo userInfo, String requesterId) throws UnknownUserException,
            UserUnauthorizedException, NonInternalUsersCannotBecomeAdminException, IllegalSpaceException {
        String userId = userInfo.getId();
        if (this.isAdmin(requesterId)) {
            UserInfo oldUserInfo = this.getUserInfo(userId);
            // Id, Name, UsedSpace
            if (!oldUserInfo.getId().equals(userId) || !oldUserInfo.getGivenName().equals(userInfo.getGivenName())
                    || !oldUserInfo.getLoginUsername().equals(userInfo.getLoginUsername())
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

    @Override
    @Transactional
    public List<UserInfo> getUsersUserInfoOnBehalfOf(int pageSize, int pageNumber, String searchString,
            String requesterId) throws UnknownUserException, UserUnauthorizedException {
        if (isAdmin(requesterId)) {
            return userRepository.findByEmailRoleInternalOrAdmin(searchString, PageRequest.of(pageNumber, pageSize))
                    .stream().map(dbUser -> dbUser.toUserInfo()).collect(Collectors.toList());
        } else {
            throw new UserUnauthorizedException();
        }
    }

    @Override
    @Transactional
    public void grantAdminRightsOnBehalfOf(String userId, String requesterId)
            throws UnknownUserException, NonInternalUsersCannotBecomeAdminException, UserUnauthorizedException {
        if (isAdmin(requesterId)) {
            grantAdminRights(userId);
        } else {
            throw new UserUnauthorizedException();
        }
    }

    boolean isRequesterIdEqualsToUserIdOrIsAnAdmin(String userId, String requesterId) throws UnknownUserException {
        return ((requesterId.equals(userId)) || (this.getDbUser(requesterId).getRole().equals(Role.ADMIN)));
    }

    boolean isUserExists(String requesterId) {
        return this.userRepository.findById(requesterId).isPresent();
    }

    /**
     * Grant admin rights to the specified user.
     */
    private void grantAdminRights(String userId)
            throws UnknownUserException, NonInternalUsersCannotBecomeAdminException {
        DBUser user = this.getDbUser(userId);

        if (!user.getRole().equals(DBUser.Role.INTERNAL)) {
            throw new NonInternalUsersCannotBecomeAdminException();
        }

        user.setRole(DBUser.Role.ADMIN);
        userRepository.save(user);
    }

    boolean isAdmin(String userId) throws UnknownUserException {
        DBUser dbUser = this.getDbUser(userId);
        return dbUser.getRole().equals(DBUser.Role.ADMIN);
    }

    boolean isAdmin(UserInfo userInfo) {
        return userInfo.getIsAdmin();
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

    @Override
    @Transactional
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

    @Override
    @Transactional
    public void setSpaceOnBehalfOf(String userId, long space, String requesterId)
            throws UnknownUserException, IllegalSpaceException, UserUnauthorizedException {
        if (isRequesterIdEqualsToUserIdOrIsAnAdmin(userId, requesterId)) {
            this.setSpace(userId, space);
        } else {
            throw new UserUnauthorizedException();
        }
    }

    /**
     * Creates an internal user with {@code email} and {@code givenName}
     * 
     * @param email
     * @param givenName
     * @throws WrongEmailStructureException if {@code email} has a wrong structure
     */
    private DBUser getOrCreateInternalUser(String email, String givenName, String username)
            throws WrongEmailStructureException {
        DBUser dbUser = null;
        if (StringUtils.validateEmailAddress(email)) {
            dbUser = this.userRepository.findOneByEmailIgnoreCase(email);
            if (dbUser == null) {
                // Not found in the database
                dbUser = this.createInternalUser(email, givenName, username);
            } else {
                // Found in the database, probably an external
                if (dbUser.getName() == null) {
                    if (givenName == null || givenName.isEmpty()) {
                        givenName = StringUtils.emailToGivenName(email);
                    }
                    dbUser.setName(givenName);
                    userRepository.save(dbUser);
                }
                if (dbUser.getUsername() == null) {
                    dbUser.setUsername(username);
                    userRepository.save(dbUser);
                }
                if (dbUser.getRole().equals(DBUser.Role.EXTERNAL)) {
                    dbUser.setRole(DBUser.Role.INTERNAL);
                    dbUser.setTotalSpace(esConfig.getDefaultUserSpace());
                    userRepository.save(dbUser);
                }
            }
            return dbUser;
        }
        throw new WrongEmailStructureException();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        DBUser dbUser = null;
        if (StringUtils.validateEmailAddress(email)) {
            dbUser = this.userRepository.findOneByEmailIgnoreCase(email);
        }
        if (dbUser == null) {
            throw new UsernameNotFoundException("Invalid email adress as username");
        }
        UserDetails userDetails = User.builder().username(email).password("n/a").roles(dbUser.getRole().toString())
                .build();
        return userDetails;
    }
}