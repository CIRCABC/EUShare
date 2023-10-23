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

import eu.europa.circabc.eushare.api.ApiKeyApiController;
import eu.europa.circabc.eushare.configuration.EushareConfiguration;
import eu.europa.circabc.eushare.exceptions.IllegalSpaceException;
import eu.europa.circabc.eushare.exceptions.NonInternalUsersCannotBecomeAdminException;
import eu.europa.circabc.eushare.exceptions.UnknownUserException;
import eu.europa.circabc.eushare.exceptions.UserUnauthorizedException;
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.exceptions.WrongEmailStructureException;
import eu.europa.circabc.eushare.model.EnumConverter;
import eu.europa.circabc.eushare.model.UserInfo;
import eu.europa.circabc.eushare.storage.repository.UserRepository;
import eu.europa.circabc.eushare.storage.dto.UserInfoDTO;
import eu.europa.circabc.eushare.storage.entity.DBUser;
import eu.europa.circabc.eushare.storage.entity.DBUser.Role;
import eu.europa.circabc.eushare.storage.entity.DBUser.Status;
import eu.europa.circabc.eushare.utils.StringUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  private Logger log = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EushareConfiguration esConfig;

  @Autowired
  private UserCreationLogService userCreationLogService;

  @Value("${spring.security.adminusers}")
  private String[] adminUsers;

  public String getAuthenticatedUserId(Authentication authentication)
      throws WrongAuthenticationException {
    if (authentication != null &&
        authentication.isAuthenticated() &&
        (authentication instanceof BearerTokenAuthentication) &&
        (authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal)) {
      BearerTokenAuthentication bearerTokenAuthentication = (BearerTokenAuthentication) authentication;

      OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuthentication.getPrincipal();
      String email = principal.getAttribute("email");
      String givenName = principal.getAttribute("name");
      String username = principal.getAttribute("username");
      String domain = (String) bearerTokenAuthentication.getTokenAttributes()
          .get("https://ecas.ec.europa.eu/claims/domain");

      if (email == null || username == null) {
        throw new WrongAuthenticationException(
            "Wrong token, cannot find email or username claim");
      }

      DBUser dbUser = null;
      try {
        dbUser = this.getOrCreateInternalUser(email, givenName, username, domain);
        return dbUser.getId();
      } catch (WrongEmailStructureException e) {
        throw new WrongAuthenticationException(e);
      }
    } else if (authentication != null && authentication.isAuthenticated()
        && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_API-KEY"))) {
      String apiKey = authentication.getPrincipal().toString();
      String encodeApiKey = ApiKeyApiController.hashApiKey(apiKey);
      DBUser dbUser = null;
      try {
        dbUser = userRepository.findOneByApiKey(encodeApiKey);

        return dbUser.getId();
      } catch (Exception e) {
        throw new WrongAuthenticationException(e);
      }
    }
    throw new WrongAuthenticationException();
  }

  private DBUser createUser(
      String email,
      String givenName,
      String username, DBUser.Role role) {
    DBUser user = DBUser.createUser(
        email,
        givenName,
        role.equals(DBUser.Role.EXTERNAL) ? esConfig.getDefaultExternalUserSpace() : esConfig.getDefaultUserSpace(),
        username, role);

    userCreationLogService.logNewUserCreation();

    for (String admin : adminUsers) {
      if (admin.equals(username))
        user.setRole(DBUser.Role.ADMIN);
    }
    return userRepository.save(user);
  }

  public DBUser getDbUser(String userId) throws UnknownUserException {
    return userRepository
        .findById(userId)
        .orElseThrow(UnknownUserException::new);
  }

  public void setAdminUsers() {
    for (String admin : adminUsers) {
      DBUser user = userRepository.findOneByUsername(admin);
      if (user != null) {
        user.setRole(DBUser.Role.ADMIN);
        user.setStatus(DBUser.Status.REGULAR);
        userRepository.save(user);
        log.warn("User {} set to admin", admin);
      }
    }
  }

  /**
   * Returns the {@code userId}'s UserInfo if the corresponding user exists.
   */
  private UserInfo getUserInfo(String userId) throws UnknownUserException {
    return this.getDbUser(userId).toUserInfo();
  }

  @Transactional
  public UserInfo getUserInfoOnBehalfOf(String userId, String requesterId)
      throws UnknownUserException, UserUnauthorizedException {
    if (isRequesterIdEqualsToUserIdOrIsAnAdmin(userId, requesterId)) {
      return getUserInfo(userId);
    } else {
      throw new UserUnauthorizedException();
    }
  }

  @Transactional
  public UserInfo setUserInfoOnBehalfOf(UserInfo userInfo, String requesterId)
      throws UnknownUserException, UserUnauthorizedException, NonInternalUsersCannotBecomeAdminException,
      IllegalSpaceException {
    String userId = userInfo.getId();
    if (this.isAdmin(requesterId)) {

      DBUser user = this.getDbUser(userId);
      DBUser.Role dbUserRole = EnumConverter.convert(userInfo.getRole(), DBUser.Role.class);
      user.setRole(dbUserRole);
      user.setTotalSpace(userInfo.getTotalSpace().longValue());
      DBUser.Status dbUserStatus = EnumConverter.convert(userInfo.getStatus(), DBUser.Status.class);
      user.setStatus(dbUserStatus);
      userRepository.save(user);

      return userInfo;
    } else {
      throw new UserUnauthorizedException();
    }
  }

  @Transactional
  public List<UserInfo> getUsersUserInfoOnBehalfOf(
      int pageSize,
      int pageNumber,
      String searchString,
      Boolean active,
      String sortBy,
      String requesterId) throws UnknownUserException, UserUnauthorizedException {
    if (isAdmin(requesterId)) {
      Direction dir = Direction.DESC;

      if (sortBy.equals("name")) {
        dir = Direction.ASC;
      }
      if (Boolean.TRUE.equals(active))
        return userRepository
            .findByEmailRoleInternalOrAdmin(
                searchString,
                PageRequest.of(pageNumber, pageSize, dir, sortBy), sortBy)
            .stream()
            .map(UserInfoDTO::toUserInfo)
            .collect(Collectors.toList());
      else
        return userRepository
            .findAllByEmailRoleInternalOrAdmin(
                searchString,
                PageRequest.of(pageNumber, pageSize, dir, sortBy), sortBy)
            .stream()
            .map(UserInfoDTO::toUserInfo)
            .collect(Collectors.toList());
    } else {
      throw new UserUnauthorizedException();
    }
  }

  /*
   * @Override
   * 
   * @Transactional
   * public void grantAdminRightsOnBehalfOf(String userId, String requesterId)
   * throws UnknownUserException, NonInternalUsersCannotBecomeAdminException,
   * UserUnauthorizedException {
   * if (isAdmin(requesterId)) {
   * grantAdminRights(userId);
   * } else {
   * throw new UserUnauthorizedException();
   * }
   * }
   */
  boolean isRequesterIdEqualsToUserIdOrIsAnAdmin(
      String userId,
      String requesterId) throws UnknownUserException {
    return ((requesterId.equals(userId)) ||
        (this.getDbUser(requesterId).getRole().equals(Role.ADMIN)));
  }

  boolean isUserExists(String requesterId) {
    return this.userRepository.findById(requesterId).isPresent();
  }

  /**
   * Grant admin rights to the specified user.
   */
  /*
   * private void grantAdminRights(String userId)
   * throws UnknownUserException, NonInternalUsersCannotBecomeAdminException {
   * DBUser user = this.getDbUser(userId);
   * 
   * if (!user.getRole().equals(DBUser.Role.INTERNAL)) {
   * throw new NonInternalUsersCannotBecomeAdminException();
   * }
   * 
   * user.setRole(DBUser.Role.ADMIN);
   * userRepository.save(user);
   * }
   */

  public boolean isAdmin(String userId) throws UnknownUserException {
    DBUser dbUser = this.getDbUser(userId);
    return dbUser.getRole().equals(DBUser.Role.ADMIN);
  }

  boolean isAdmin(UserInfo userInfo) {
    return userInfo.getIsAdmin();
  }

  /**
   * Creates an internal user with {@code email} and {@code givenName}
   *
   * @param email
   * @param givenName
   * @throws WrongEmailStructureException if {@code email} has a wrong structure
   */
  private DBUser getOrCreateInternalUser(
      String email,
      String givenName,
      String username,
      String domain) throws WrongEmailStructureException {
    DBUser dbUser = null;
    if (StringUtils.validateEmailAddress(email)) {
      dbUser = this.userRepository.findOneByUsername(username);
      if (dbUser == null) {
        // Not found in the database
        if ("external".equals(domain) || username.startsWith("n00%"))
          dbUser = this.createUser(email, givenName, username, Role.EXTERNAL);
        else
          dbUser = this.createUser(email, givenName, username, Role.INTERNAL);

        //
      } else {
        updateUser(email, givenName, username, dbUser);
      }
      return dbUser;
    }
    throw new WrongEmailStructureException();
  }

  private void updateUser(
      String email,
      String givenName,
      String username,
      DBUser dbUser) {
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
    dbUser.setLastLogged(LocalDateTime.now());
  }

  public void saveUser(DBUser user) {
    userRepository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String email)
      throws UsernameNotFoundException {
    DBUser dbUser = null;
    if (StringUtils.validateEmailAddress(email)) {
      dbUser = this.userRepository.findOneByEmailIgnoreCase(email);
    }
    if (dbUser == null) {
      throw new UsernameNotFoundException("Invalid email adress as username");
    }
    return User
        .builder()
        .username(email)
        .password("n/a")
        .roles(dbUser.getRole().toString())
        .build();
  }
}
