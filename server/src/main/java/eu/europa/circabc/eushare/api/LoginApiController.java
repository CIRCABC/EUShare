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
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.model.UserResult;
import eu.europa.circabc.eushare.services.UserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

@javax.annotation.Generated(
  value = "org.openapitools.codegen.languages.SpringCodegen"
)
@Controller
@RequestMapping("${openapi.easyShare.base-path:}")
public class LoginApiController implements LoginApi {

  private Logger log = LoggerFactory.getLogger(LoginApiController.class);

  private final NativeWebRequest request;

  @Autowired
  private UserService userService;

  @org.springframework.beans.factory.annotation.Autowired
  public LoginApiController(NativeWebRequest request) {
    this.request = request;
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<UserResult> postLogin() {
    try {
      Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
      
      String userId = userService.getAuthenticatedUserId(authentication);
      return new ResponseEntity<>(
        (new UserResult()).userId(userId),
        HttpStatus.OK
      );
    } catch (WrongAuthenticationException exc) {
      log.debug("wrong authentication !");
      ResponseStatusException responseStatusException = new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        HttpErrorAnswerBuilder.build401EmptyToString(),
        exc
      );
      throw responseStatusException;
    }
  }
}
