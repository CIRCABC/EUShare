/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;

import com.circabc.easyshare.TestHelper;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.model.Status;
import com.circabc.easyshare.security.OIDCJwtAuthenticationConverter;
import com.circabc.easyshare.services.UserService;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@WebMvcTest(LoginApiController.class)
public class LoginApiControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OIDCJwtAuthenticationConverter jwtAuthenticationConverter;

        @MockBean
        private JwtDecoder jwtDecoder;

        // @MockBean
        // private AuthenticationProvider jwtAuthenticationProvider;

        @MockBean
        private UserService service;

        // @org.junit.Before
        // public void setupUserDetails() {
        // UserDetails userDetails = new User("username", "password",
        // Collections.emptySet());
        // when(service.loadUserByUsername(anyString())).thenReturn(userDetails);
        // }

        @Test
        @WithMockUser(username = "username", password = "n/a", roles = "INTERNAL")
        public void postLoginResponse200() throws Exception {// NOSONAR
                String token = LoginApiControllerTest.createToken("username");
                // token =
                // "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxdDl3bW5fNDhsUWRienZtbzNXdmhVVzlsZlZ4dTdoMkJsY0FFYTJ4X3BFIn0.eyJqdGkiOiJhZmMwODA4MS05MzM2LTRlMjYtYTc3YS02NzY5M2ZjYjg1Y2YiLCJleHAiOjE1NzMxMTY2NzAsIm5iZiI6MCwiaWF0IjoxNTczMTE2MzcwLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvaGVyb2VzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImI1NTgzMzQxLWMzOGItNGM3MC05YmM5LTI0ZDRjNDE4NzFkZCIsInR5cCI6IkJlYXJlciIsImF6cCI6InNwYS1oZXJvZXMiLCJub25jZSI6IjczNU1lOTZtMXZQQTRpaExEYXlxY0c3WnYtcXdobkE5RE5xcjdLMW1jaG5rNiIsImF1dGhfdGltZSI6MTU3MzExNDU3OCwic2Vzc2lvbl9zdGF0ZSI6IjRmZTBiMTEwLWMyNzktNGIyYy1hNjlhLWQ0N2Q5ZGFmMWFkMSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBvZmZsaW5lX2FjY2VzcyBwcm9maWxlIGVtYWlsIGhlcm9lcyIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6InJhY2hlbCByYWNoZWwiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJyYWNoZWwiLCJnaXZlbl9uYW1lIjoicmFjaGVsIiwiZmFtaWx5X25hbWUiOiJyYWNoZWwiLCJlbWFpbCI6InJhY2hlbEByYWNoZWwuY29tIn0.KAbh2fuWKmZFQTMkidFIzm6U3AqokECvHgh8rA1kMNMuznwKoMX3_7vDSX_6Rkhl8llIqmbVWusHhMdWwo0dLb4BPMF0NYEC7zLU5M8GAMl2yi7KRJaetQCm1SwFy12KJNuByvL32MfEOPqOTfMQrN6ili7g0OYQ7b5thTgB_PvDAO9bKgZ2cvlqati2F1DRCntBAjqxXjdoGVnddmN3cyP_JhhWwCsrknj4DBMLshWzqjBktLjwyEOHdzVF6x5iMqzBTlf-BdeYhKxtPK3TzBxNJOizG2zrKcW1HR2-P8DQcDpzQOQhG-ukV3Fo69B9LDUVqPlpVxRvEensgbaEBQ";

                UserDetails userDetails = User.builder().username("username").password("n/a").roles("Internal").build();
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, "n/a",
                                userDetails.getAuthorities());
                when(jwtAuthenticationConverter.convert(any(Jwt.class))).thenReturn(authentication);
                // when(jwtAuthenticationProvider.authenticate(authentication)).thenReturn(authentication);
                // when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn("testId");
                // when(jwtDecoder.decode(anyString())).thenReturn(SignedJWT.parse(token));
                this.mockMvc.perform(MockMvcRequestBuilders.post("/login").header("Authorization", "Bearer " + token)
                                .content("").contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(content().string(containsString("testId")));
        }

        @Test
        public void postLoginResponse400() throws Exception {// NOSONAR
                Status status = new Status();
                status.setCode(400);
                this.mockMvc.perform(MockMvcRequestBuilders.post("/login").content("")
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN)).andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void postLoginResponse401() throws Exception {// NOSONAR
                Status status = new Status();
                status.setCode(401);
                Credentials credentials = new Credentials();
                credentials.setEmail("email");
                credentials.setPassword("password");
                when(service.getAuthenticatedUserId(any(Credentials.class)))
                                .thenThrow(new WrongAuthenticationException());
                this.mockMvc.perform(MockMvcRequestBuilders.post("/login").content(TestHelper.asJsonString(credentials))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN)).andDo(print())
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void postLoginResponse500() throws Exception {// NOSONAR
                Status status = new Status();
                status.setCode(500);
                Credentials credentials = new Credentials();
                credentials.setEmail("username");
                credentials.setPassword("password");
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenThrow(new NullPointerException());
                this.mockMvc.perform(MockMvcRequestBuilders.post("/login").content(TestHelper.asJsonString(credentials))
                                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                                .andDo(print()).andExpect(status().isInternalServerError())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        public static String createToken(String username) throws Exception {
                JWTClaimsSet claims = new JWTClaimsSet.Builder().claim("email", "email@email.com")
                                .claim("name", "username").claim("groups", "none")
                                .claim("scope", "openid offline_access profile email").build();
                JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256),
                                new Payload(claims.toJSONObject()));
                // We need a 256-bit key for HS256 which must be pre-shared
                byte[] sharedKey = new byte[32];
                new SecureRandom().nextBytes(sharedKey);

                // Apply the HMAC to the JWS object
                jwsObject.sign(new MACSigner(sharedKey));

                // Output in URL-safe format
                System.out.println(jwsObject.serialize());
                return jwsObject.serialize();
        }

}