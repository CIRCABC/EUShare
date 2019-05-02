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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@WebMvcTest(MeApiController.class)
public class MeApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Test
    public void getUserInfo200() throws Exception {
        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("username");
        UserInfo fakeUserInfo = new UserInfo();
        fakeUserInfo.setId("username");
        fakeUserInfo.setIsAdmin(false);
        fakeUserInfo.setSharedFiles(Collections.emptyList());
        fakeUserInfo.setTotalSpace(new BigDecimal(1024*1204));
        fakeUserInfo.setUploadedFiles(Collections.emptyList());
        fakeUserInfo.setUsedSpace(new BigDecimal(0));
        when(service.getUserInfoOnBehalfOf(anyString(),anyString())).thenReturn(fakeUserInfo);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/me/userInfo").header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString(TestHelper.asJsonString(fakeUserInfo))));
    }

    @Test
    public void getUserInfo403ForWrongCredentials() throws Exception {
        final String userWrongCredentialsInAuthorizationHeader = "username:password";
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");
        // userService knows that the user has not provided good credentials
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenThrow(new WrongAuthenticationException());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/me/userInfo").header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(userWrongCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden()).andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUserInfo403ForNoCredentials() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");
        this.mockMvc.perform(MockMvcRequestBuilders.get("/me/userInfo")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden()).andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUserInfo500ForUnknownUserException() throws Exception {
        final String userCredentialsInAuthorizationHeader = "username:password";
        Status status = new Status();
        status.setCode(500);
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("username");
        when(service.getUserInfoOnBehalfOf(anyString(),anyString())).thenThrow(new UserUnauthorizedException());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/me/userInfo").header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

  
    @Test
    public void getUserInfo500ForNullPointerException() throws Exception {
        final String userCredentialsInAuthorizationHeader = "username:password";
        Status status = new Status();
        status.setCode(500);
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("username");
        when(service.getUserInfoOnBehalfOf(anyString(),anyString())).thenThrow(new NullPointerException());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/me/userInfo").header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUserInfo500ForUserNotFoundException() throws Exception {
        final String userCredentialsInAuthorizationHeader = "username:password";
        Status status = new Status();
        status.setCode(500);
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("username");
        when(service.getUserInfoOnBehalfOf(anyString(),anyString())).thenThrow(new UnknownUserException());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/me/userInfo").header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isInternalServerError()).andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }
}