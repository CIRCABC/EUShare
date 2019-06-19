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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.model.Credentials;
import com.circabc.easyshare.model.Status;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.services.UserService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@WebMvcTest(UsersApiController.class)
public class UsersApiControllerTest {

    private final String fakeAuthenticatedUserId = "fakeAuthenticatedUserId";
    private final String userCredentialsInAuthorizationHeader = "username:password";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Test
    public void getUsersUserInfo200() throws Exception { // NOSONAR
        UserInfo userInfo = new UserInfo();
        userInfo.setId("id");
        userInfo.setIsAdmin(true);
        userInfo.setName("name");
        userInfo.setTotalSpace(new BigDecimal(1024));
        userInfo.setUsedSpace(new BigDecimal(512));
        List<UserInfo> userInfoList = Arrays.asList(userInfo);
        when(service.getUsersUserInfoOnBehalfOf(anyInt(), anyInt(), anyString(), anyString())).thenReturn(userInfoList);
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/users/userInfo")
                .param("pageSize", "10")
                .param("pageNumber", "1")
                .param("searchString", "email@email.com")
                .header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(TestHelper.asJsonString(userInfoList))));
    }

    @Test
    public void getUsersUserInfo400() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(400);
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/users/userInfo")
                .param("pageSize", "10")
                .param("searchString", "email@email.com")
                .header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUsersUserInfo401ForNoAuthentication() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(401);
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/users/userInfo")
                .param("pageSize", "10")
                .param("pageNumber", "1")
                .param("searchString", "email@email.com")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUsersUserInfo401ForWrongAuthentication() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(401);
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenThrow(new WrongAuthenticationException());
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/users/userInfo")
        .param("pageSize", "10")
        .param("pageNumber", "1")
        .param("searchString", "email@email.com")
        .header("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUsersUserInfo403() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");
        when(service.getUsersUserInfoOnBehalfOf(anyInt(), anyInt(), anyString(), anyString())).thenThrow(new UserUnauthorizedException());
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/users/userInfo")
        .param("pageSize", "10")
        .param("pageNumber", "1")
        .param("searchString", "email@email.com")
        .header("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isForbidden())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUsersUserInfo500() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(500);
        when(service.getUsersUserInfoOnBehalfOf(anyInt(), anyInt(), anyString(), anyString())).thenThrow(new UnknownUserException());
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/users/userInfo")
        .param("pageSize", "10")
        .param("pageNumber", "1")
        .param("searchString", "email@email.com")
        .header("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isInternalServerError())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }
}