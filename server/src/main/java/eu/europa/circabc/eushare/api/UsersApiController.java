/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.api;

import eu.europa.circabc.eushare.error.HttpErrorAnswerBuilder;
import eu.europa.circabc.eushare.exceptions.UnknownUserException;
import eu.europa.circabc.eushare.exceptions.UserUnauthorizedException;
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.model.UserInfo;
import eu.europa.circabc.eushare.services.UserService;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

@javax.annotation.Generated(
  value = "org.openapitools.codegen.languages.SpringCodegen"
)
@Controller
@RequestMapping("${openapi.easyShare.base-path:}")
public class UsersApiController implements UsersApi {

  private final NativeWebRequest request;

  @Autowired
  private UserService userService;

  @org.springframework.beans.factory.annotation.Autowired
  public UsersApiController(NativeWebRequest request) {
    this.request = request;
  }

 
  @Override
  public ResponseEntity<List<UserInfo>> getUsersUserInfo(
    @RequestParam(value = "pageSize") Integer pageSize,
    @RequestParam(value = "pageNumber") Integer pageNumber,
    @RequestParam(value = "searchString") String searchString,
    @RequestParam(value = "active") Boolean active,
    @RequestParam(value = "sortBy") String sortBy
    
  ) {
    try {
      Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
      String requesterId = userService.getAuthenticatedUserId(authentication);

      return new ResponseEntity<>(
        userService.getUsersUserInfoOnBehalfOf(
          pageSize,
          pageNumber,
          searchString,
          active,
          sortBy,
          requesterId
        ),
        HttpStatus.OK
      );
    } catch (WrongAuthenticationException exc) {
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        HttpErrorAnswerBuilder.build401EmptyToString(),
        exc
      );
    } catch (UserUnauthorizedException exc) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
        exc
      );
    } catch (UnknownUserException e) {
      throw new ResponseStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        HttpErrorAnswerBuilder.build500EmptyToString(),
        e
      );
    }
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }
}
