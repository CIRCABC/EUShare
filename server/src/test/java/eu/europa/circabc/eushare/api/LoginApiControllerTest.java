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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;

import eu.europa.circabc.eushare.api.LoginApiController;
import eu.europa.circabc.eushare.model.Status;
import eu.europa.circabc.eushare.services.UserService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@WebMvcTest(LoginApiController.class)
public class LoginApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpaqueTokenIntrospector opaqueTokenIntrospector;

    @MockBean
    private UserService service;

    @Test
    public void postLoginResponse200() throws Exception {// NOSONAR
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();
        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal("username",
                attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn("testId");

        String token = "StupidToken";

        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/login").header("Authorization", "Bearer " + token).content("")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("testId")));
    }

    @Test
    public void postLoginResponse401() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(401);
        when(opaqueTokenIntrospector.introspect(anyString())).thenThrow(new OAuth2IntrospectionException(""));
        String token = "StupidToken";
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/login").header("Authorization", "Bearer " + token).content("")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(LoginApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postLoginResponse500() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(500);
        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();
        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal("username",
                attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenThrow(new NullPointerException());

        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/login").header("Authorization", "Bearer " + token).content("")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(LoginApiControllerTest.asJsonString(status))));
    }

    public static String createToken(String username) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder().issueTime(new Date()).expirationTime(new Date())
                .jwtID("afc08081-9336-4e26-a77a-67693fcb85cf")
                // .claim("iat", 1573116370)
                // .claim("exp",1573116670)
                .claim("email", "email@email.com").claim("name", "username").claim("groups", "none")
                .claim("scope", "openid offline_access profile email").build();
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(claims.toJSONObject()));
        // We need a 256-bit key for HS256 which must be pre-shared
        byte[] sharedKey = new byte[32];
        new SecureRandom().nextBytes(sharedKey);

        // Apply the HMAC to the JWS object
        jwsObject.sign(new MACSigner(sharedKey));

        // Output in URL-safe format
        System.out.println(jwsObject.serialize());
        return jwsObject.serialize();
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);// NOSONAR
        }
    }

}