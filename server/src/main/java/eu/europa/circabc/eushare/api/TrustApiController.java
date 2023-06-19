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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import eu.europa.circabc.eushare.error.HttpErrorAnswerBuilder;
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.model.Recipient;
import eu.europa.circabc.eushare.model.TrustRequest;
import eu.europa.circabc.eushare.services.EmailService;
import eu.europa.circabc.eushare.services.UserService;
import eu.europa.circabc.eushare.storage.DBTrust;
import eu.europa.circabc.eushare.storage.DBUser;
import eu.europa.circabc.eushare.storage.TrustRepository;
import eu.europa.circabc.eushare.storage.UserRepository;
import eu.europa.circabc.eushare.storage.DBUser.Role;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@Api(value = "trust", description = "the trust API")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class TrustApiController implements TrustApi {

        private Logger log = LoggerFactory.getLogger(UserService.class);

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private EmailService emailService;

        @Autowired
        private TrustRepository trustRepository;

        @Autowired
        public UserService userService;

        private final NativeWebRequest request;

        public TrustApiController(NativeWebRequest request) {
                this.request = request;
        }

        @Override
        public Optional<NativeWebRequest> getRequest() {
                return Optional.ofNullable(request);
        }

        @Override
        @ApiOperation(value = "Approve Trust Request", nickname = "approveTrustRequest", notes = "", response = TrustRequest.class, tags = {
                        "Trust", })
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successful Operation", response = TrustRequest.class) })
        @PutMapping(value = "/trust/{id}", produces = { "application/json" })
        public ResponseEntity<TrustRequest> approveTrustRequest(
                        @ApiParam(value = "", required = true) @PathVariable("id") String id,
                        @NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "approved", required = true) Boolean approved,
                        @ApiParam(value = "") @Valid @RequestParam(value = "reason", required = false) String reason) {
                // Implement your logic here to approve the trust request
                DBTrust trust = trustRepository.findOneById(id);
                trust.setApproved(approved);
                trustRepository.save(trust);
                // Set the properties of the approved request

                DBUser user = userRepository.findOneByEmailIgnoreCase(trust.getEmail());
                if (user != null) {
                        String message;
                        if (approved && user.getRole().equals(Role.EXTERNAL)) {
                                message = "Your trust request has been accepted by CIRCABC-Share administrator, you can now share some files.";
                                user.setRole(Role.TRUSTED_EXTERNAL);
                        }
                        else {
                                message = "We are sorry but your trust request has been denied by CIRCABC-Share administrator for the following reason : " +reason;
                                user.setRole(Role.EXTERNAL);
                        }
                        userRepository.save(user);
                        
                        try {
                                
                                this.emailService.sendNotification(user.getEmail(), message);
                        } catch (MessagingException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                }
                return ResponseEntity.ok(trust.toTrustRequest());
        }

        @Override
        @ApiOperation(value = "Delete Trust Request", nickname = "deleteTrustRequest", notes = "", response = TrustRequest.class, tags = {
                        "Trust", })
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successful Operation", response = TrustRequest.class) })
        @DeleteMapping(value = "/trust/{id}", produces = { "application/json" })
        public ResponseEntity<TrustRequest> deleteTrustRequest(
                        @ApiParam(value = "", required = true) @PathVariable("id") String id) {
                // Implement your logic here to delete the trust request
                DBTrust trust = trustRepository.findOneById(id);

                trustRepository.delete(trust);
                return ResponseEntity.ok().build();
        }

        @Override
        @ApiOperation(value = "Get Trust Request List", nickname = "getTrustRequestList", notes = "", response = TrustRequest.class, responseContainer = "List", tags = {
                        "Trust", })
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successful Operation", response = TrustRequest.class, responseContainer = "List") })
        @GetMapping(value = "/trust", produces = { "application/json" })
        public ResponseEntity<List<TrustRequest>> getTrustRequestList() {
                // Implement your logic here to get the list of trust requests
                List<DBTrust> dbTrustList = trustRepository.findAll();
                List<TrustRequest> trustRequests = new ArrayList<>();

                for (DBTrust dbTrust : dbTrustList) {
                        TrustRequest trustRequest = dbTrust.toTrustRequest();
                        trustRequests.add(trustRequest);
                }

                return ResponseEntity.ok(trustRequests);
        }

        @Override
        @ApiOperation(value = "Send Trust Request", nickname = "sendTrustRequest", notes = "", response = TrustRequest.class, tags = {
                        "Trust", })
        @ApiResponses(value = {
                        @ApiResponse(code = 200, message = "Successful Operation", response = TrustRequest.class) })
        @PostMapping(value = "/trust", produces = { "application/json" }, consumes = { "application/json" })
        public ResponseEntity<TrustRequest> sendTrustRequest(
                        @ApiParam(value = "", required = true) @Valid @RequestBody TrustRequest trustRequest) {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();
                try {
                        String requesterId = userService.getAuthenticatedUserId(authentication);
                        DBUser user = userRepository.findOneById(requesterId);
                        trustRequest.setName(user.getName());
                        trustRequest.setUsername(user.getUsername());
                        trustRequest.setApproved(false);
                        trustRequest.setEmail(user.getEmail());
                        trustRequest.setRequestDateTime(OffsetDateTime.now());
                        DBTrust trust = DBTrust.fromTrustRequest(trustRequest);
                        trustRepository.save(trust);
                } catch (WrongAuthenticationException e) {
                        log.debug("wrong authentication !");
                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        HttpErrorAnswerBuilder.build401EmptyToString(),
                                        e);
                }

                return ResponseEntity.ok(trustRequest);
        }
}
