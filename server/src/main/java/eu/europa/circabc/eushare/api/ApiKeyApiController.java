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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import eu.europa.circabc.eushare.error.HttpErrorAnswerBuilder;
import eu.europa.circabc.eushare.exceptions.UnknownUserException;
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.model.ApiKey;
import eu.europa.circabc.eushare.model.UserInfo;
import eu.europa.circabc.eushare.services.UserService;
import eu.europa.circabc.eushare.storage.DBUser;
import eu.europa.circabc.eushare.storage.UserRepository;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.cIRCABCShare.base-path:}")
public class ApiKeyApiController implements ApikeyApi {

    private static final Logger log = LoggerFactory.getLogger(
            ApiKeyApiController.class);

    private final NativeWebRequest request;

    @Autowired
    public UserService userService;

    @org.springframework.beans.factory.annotation.Autowired
    public ApiKeyApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<ApiKey> getApiKey() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        try {
            String requesterId = userService.getAuthenticatedUserId(authentication);
            DBUser user = userService.getDbUser(requesterId);
            if (! (user.getRole().name().equals(UserInfo.RoleEnum.ADMIN.name())  ||  user.getRole().name().equals(UserInfo.RoleEnum.API_KEY.name()))  )
               throw new WrongAuthenticationException("Not allowed")  ;
                  
            String apiKey = generateApiKey();

            user.setApiKey( hashApiKey(apiKey)) ;
            userService.saveUser(user);

            ApiKey apiKeyObj = new ApiKey();
            apiKeyObj.setApikey(apiKey);
            return new ResponseEntity<ApiKey>(apiKeyObj, HttpStatus.OK);

        } catch (WrongAuthenticationException e) {
            log.debug("wrong authentication !");
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    HttpErrorAnswerBuilder.build401EmptyToString(),
                    e);
        } catch (UnknownUserException e) {
            log.warn(e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    HttpErrorAnswerBuilder.build404EmptyToString(),
                    e);
        }
    }

    private String generateApiKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] apiKeyBytes = new byte[24];
        secureRandom.nextBytes(apiKeyBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(apiKeyBytes);
    }



    public static String hashApiKey(String apiKey) {
        try {
          MessageDigest digest = MessageDigest.getInstance("SHA-256");
          byte[] encodedHash = digest.digest(apiKey.getBytes(StandardCharsets.UTF_8));
          StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
          for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
          }
          return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
        }
        return null;
      }
}
