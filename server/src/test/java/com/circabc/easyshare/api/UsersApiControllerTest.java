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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.model.Status;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.services.UserService;
import com.circabc.easyshare.storage.DBUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@WebMvcTest(UsersApiController.class)
public class UsersApiControllerTest {

    private final String fakeAuthenticatedUserId = "fakeAuthenticatedUserId";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpaqueTokenIntrospector opaqueTokenIntrospector;

    @MockBean
    private UserService service;

    @Test
    public void getUsersUserInfo200() throws Exception { // NOSONAR
        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("name", "name SURNAME");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        UserInfo userInfo = new UserInfo();
        userInfo.setId("id");
        userInfo.setIsAdmin(true);
        userInfo.setGivenName("name SURNAME");
        userInfo.setLoginUsername("loginUsername");
        userInfo.setEmail("email@email.com");
        userInfo.setTotalSpace(new BigDecimal(1024));
        userInfo.setUsedSpace(new BigDecimal(512));
        List<UserInfo> userInfoList = Arrays.asList(userInfo);

        UserDetails userDetails = User.builder().username("admin").password("admin").roles(DBUser.Role.ADMIN.toString())
                .build();
        when(service.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        when(service.getUsersUserInfoOnBehalfOf(anyInt(), anyInt(), anyString(), anyString())).thenReturn(userInfoList);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/users/userInfo").param("pageSize", "10").param("pageNumber", "1")
                        .param("searchString", "email@email.com")
                        .header("Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(UsersApiControllerTest.asJsonString(userInfoList))));
    }

    @Test
    public void getUsersUserInfo400() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(400);
        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("name", "name SURNAME");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/users/userInfo").param("pageSize", "10")
                        .param("searchString", "email@email.com")
                        .header("Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(UsersApiControllerTest.asJsonString(status))));
    }

    @Test
    public void getUsersUserInfo401ForNoAuthentication() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(401);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/users/userInfo").param("pageSize", "10").param("pageNumber", "1")
                        .param("searchString", "email@email.com").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(UsersApiControllerTest.asJsonString(status))));
    }

    @Test
    public void getUsersUserInfo401ForWrongAuthentication() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(401);
        String token = "StupidToken";
        when(opaqueTokenIntrospector.introspect(anyString())).thenThrow(new OAuth2IntrospectionException(""));

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/users/userInfo").param("pageSize", "10").param("pageNumber", "1")
                        .param("searchString", "email@email.com")
                        .header("Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(UsersApiControllerTest.asJsonString(status))));
    }

    @Test
    public void getUsersUserInfo403() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");

        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("name", "name SURNAME");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);
        
        when(service.getUsersUserInfoOnBehalfOf(anyInt(), anyInt(), anyString(), anyString()))
                .thenThrow(new UserUnauthorizedException());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/users/userInfo").param("pageSize", "10").param("pageNumber", "1")
                        .param("searchString", "email@email.com")
                        .header("Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(UsersApiControllerTest.asJsonString(status))));
    }

    @Test
    public void getUsersUserInfo500() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(500);

        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("name", "name SURNAME");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        when(service.getUsersUserInfoOnBehalfOf(anyInt(), anyInt(), anyString(), anyString()))
                .thenThrow(new UnknownUserException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/users/userInfo").param("pageSize", "10").param("pageNumber", "1")
                        .param("searchString", "email@email.com")
                        .header("Authorization",
                                "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(UsersApiControllerTest.asJsonString(status))));
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