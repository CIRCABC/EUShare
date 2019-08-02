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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.model.Credentials;
import com.circabc.easyshare.model.Status;
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

@RunWith(SpringRunner.class)
@WebMvcTest(LoginApiController.class)
public class LoginApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Test
    public void postLoginResponse200() throws Exception {//NOSONAR
        Credentials credentials = new Credentials();
        credentials.setEmail("email");
        credentials.setPassword("password");
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("testId");
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/login").content(TestHelper.asJsonString(credentials))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("testId")));
    }

    @Test
    public void postLoginResponse400() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(400);
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/login").content("").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void postLoginResponse401() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(401);
        Credentials credentials = new Credentials();
        credentials.setEmail("email");
        credentials.setPassword("password");
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenThrow(new WrongAuthenticationException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/login").content(TestHelper.asJsonString(credentials))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void postLoginResponse500() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(500);
        Credentials credentials = new Credentials();
        credentials.setEmail("email");
        credentials.setPassword("password");
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenThrow(new NullPointerException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/login").content(TestHelper.asJsonString(credentials))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

}