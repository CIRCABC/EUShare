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

import com.circabc.easyshare.services.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;

import com.circabc.easyshare.exceptions.ExternalUserCannotBeAdminException;
import com.circabc.easyshare.exceptions.IllegalSpaceException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.model.Credentials;
import com.circabc.easyshare.model.Status;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.model.UserSpace;

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
@WebMvcTest(UserApiController.class)
public class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Test
    public void getUserUserInfo200() throws Exception {
        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

        final String fakeSearchedUserId = "fakeSearchedUserId";

        UserInfo fakeUserInfo = new UserInfo();
        fakeUserInfo.setId(fakeSearchedUserId);
        fakeUserInfo.setIsAdmin(false);
        fakeUserInfo.setSharedFiles(Collections.emptyList());
        fakeUserInfo.setTotalSpace(new BigDecimal(1024 * 1204));
        fakeUserInfo.setUploadedFiles(Collections.emptyList());
        fakeUserInfo.setUsedSpace(new BigDecimal(0));

        when(service.getUserInfoOnBehalfOf(anyString(), anyString())).thenReturn(fakeUserInfo);
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/userInfo").header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(TestHelper.asJsonString(fakeUserInfo))));
    }

    @Test
    public void getUserInfo403ForUnauthorizedUser() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");

        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
        final String fakeSearchedUserId = "fakeSearchedUserId";
        when(service.getUserInfoOnBehalfOf(anyString(), anyString())).thenThrow(new UserUnauthorizedException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/userInfo").header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUserInfo403ForWrongCredentials() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");
        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenThrow(new WrongAuthenticationException());
        final String fakeSearchedUserId = "fakeSearchedUserId";

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/userInfo").header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUserInfo403ForNoCredentials() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");
        final String fakeSearchedUserId = "fakeSearchedUserId";
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/userInfo")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUserInfo404() throws Exception {
        Status status = new Status();
        status.setCode(404);

        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

        final String fakeSearchedUserId = "fakeSearchedUserId";
        when(service.getUserInfoOnBehalfOf(anyString(), anyString())).thenThrow(new UnknownUserException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/userInfo").header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUserInfo500() throws Exception {
        Status status = new Status();
        status.setCode(500);

        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

        final String fakeSearchedUserId = "fakeSearchedUserId";
        when(service.getUserInfoOnBehalfOf(anyString(), anyString())).thenThrow(new NullPointerException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/userInfo").header("Authorization",
                        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUserUserSpace200() throws Exception {
        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

        final String fakeSearchedUserId = "fakeSearchedUserId";

        UserSpace fakeUserSpace = new UserSpace();
        fakeUserSpace.setTotalSpace(new BigDecimal(1024 * 1204));
        fakeUserSpace.setUsedSpace(new BigDecimal(0));

        when(service.getUserSpaceOnBehalfOf(anyString(), anyString())).thenReturn(fakeUserSpace);
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/userInfo/userSpace")
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(TestHelper.asJsonString(fakeUserSpace))));
    }

    @Test
    public void getUserUserSpace403ForUnauthorizedUser() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");

        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

        final String fakeSearchedUserId = "fakeSearchedUserId";
        when(service.getUserSpaceOnBehalfOf(anyString(), anyString())).thenThrow(new UserUnauthorizedException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/userInfo/userSpace")
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUserUserSpace403ForWrongCredentials() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");

        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenThrow(new WrongAuthenticationException());

        final String fakeSearchedUserId = "fakeSearchedUserId";
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/userInfo/userSpace")
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUserUserSpace403ForNoCredentials() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");

        final String fakeSearchedUserId = "fakeSearchedUserId";
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/userInfo/userSpace")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUserSpace404() throws Exception {
        Status status = new Status();
        status.setCode(404);

        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

        final String fakeSearchedUserId = "fakeSearchedUserId";

        when(service.getUserSpaceOnBehalfOf(anyString(), anyString())).thenThrow(new UnknownUserException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/userInfo/userSpace")
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getUserSpace500() throws Exception {
        Status status = new Status();
        status.setCode(500);

        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

        final String fakeSearchedUserId = "fakeSearchedUserId";

        when(service.getUserSpaceOnBehalfOf(anyString(), anyString())).thenThrow(new NullPointerException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/userInfo/userSpace")
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    
    @Test
    public void putUserIsAdmin200() throws Exception {
        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
        final String fakeSearchedUserId = "fakeSearchedUserId";
        doNothing().when(service).grantAdminRightsOnBehalfOf(anyString(), anyString());
        doNothing().when(service).revokeAdminRightsOnBehalfOf(anyString(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/isAdmin")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content("true".getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/isAdmin")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content("false".getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void putUserIsAdmin400() throws Exception {
        Status status = new Status();
        status.setCode(400);
        final String userCredentialsInAuthorizationHeader = "username:password";
        final String fakeSearchedUserId = "fakeSearchedUserId";
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/isAdmin")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content("".getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserIsAdmin403ForWrongAuthentication() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");
        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenThrow(new WrongAuthenticationException());

        final String fakeSearchedUserId = "fakeSearchedUserId";

        doNothing().when(service).grantAdminRightsOnBehalfOf(anyString(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/isAdmin")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content("true".getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isForbidden())
                        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserIsAdmin403FoNoAuthentication() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenThrow(new WrongAuthenticationException());

        final String fakeSearchedUserId = "fakeSearchedUserId";

        doNothing().when(service).grantAdminRightsOnBehalfOf(anyString(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/isAdmin")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content("true".getBytes("utf-8"))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isForbidden())
                        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserIsAdmin403ForUserUnauthorized() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
        doThrow(new UserUnauthorizedException()).when(service).grantAdminRightsOnBehalfOf(anyString(), anyString());
        final String fakeSearchedUserId = "fakeSearchedUserId";

        doNothing().when(service).grantAdminRightsOnBehalfOf(anyString(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/isAdmin")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content("true".getBytes("utf-8"))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isForbidden())
                        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserIsAdmin404ForUnknownUser() throws Exception {
        Status status = new Status();
        status.setCode(404);

        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
        final String fakeSearchedUserId = "fakeSearchedUserId";
        doThrow(new UnknownUserException()).when(service).grantAdminRightsOnBehalfOf(anyString(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/isAdmin")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content("true".getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isNotFound())
                        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserIsAdmin404ForExternalUserCannotBeAdmin() throws Exception {
        Status status = new Status();
        status.setCode(404);

        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
        final String fakeSearchedUserId = "fakeSearchedUserId";
        doThrow(new ExternalUserCannotBeAdminException()).when(service).grantAdminRightsOnBehalfOf(anyString(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/isAdmin")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content("true".getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isNotFound())
                        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserIsAdmin500() throws Exception {
        Status status = new Status();
        status.setCode(500);

        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
        final String fakeSearchedUserId = "fakeSearchedUserId";
        doThrow(new NullPointerException()).when(service).grantAdminRightsOnBehalfOf(anyString(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/isAdmin")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content("true".getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }


    
    @Test
    public void putUserTotalSpace200() throws Exception {
        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

        final String fakeSearchedUserId = "fakeSearchedUserId";

        doNothing().when(service).setSpaceOnBehalfOf(anyString(), anyLong(), anyString());

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/userSpace/totalSpace")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content((new BigDecimal(1024)).toString().getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void putUserTotalSpace400() throws Exception {
        Status status = new Status();
        status.setCode(400);

        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

        final String fakeSearchedUserId = "fakeSearchedUserId";

        doNothing().when(service).setSpaceOnBehalfOf(anyString(), anyLong(), anyString());

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/userSpace/totalSpace")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content("wrongType".toString().getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserTotalSpace400Empty() throws Exception {
        Status status = new Status();
        status.setCode(400);

        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

        final String fakeSearchedUserId = "fakeSearchedUserId";

        doNothing().when(service).setSpaceOnBehalfOf(anyString(), anyLong(), anyString());

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/userSpace/totalSpace")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content("".toString().getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isBadRequest())
                        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserTotalSpace403ForNoAuthentication() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");
        final String fakeSearchedUserId = "fakeSearchedUserId";
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/userSpace/totalSpace")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content((new BigDecimal(1024)).toString().getBytes("utf-8"))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isForbidden())
                        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserTotalSpace403ForWrongAuthentication() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");
        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenThrow(new WrongAuthenticationException());
        final String fakeSearchedUserId = "fakeSearchedUserId";

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/userSpace/totalSpace")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content((new BigDecimal(1024)).toString().getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserTotalSpace403ForIllegalSpace() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");
        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
        final String fakeSearchedUserId = "fakeSearchedUserId";
        doThrow(new IllegalSpaceException()).when(service).setSpaceOnBehalfOf(anyString(), anyLong(), anyString());

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/userSpace/totalSpace")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content((new BigDecimal(1024)).toString().getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserTotalSpace403ForUserUnauthorized() throws Exception {
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");
        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
        final String fakeSearchedUserId = "fakeSearchedUserId";
        doThrow(new UserUnauthorizedException()).when(service).setSpaceOnBehalfOf(anyString(), anyLong(), anyString());

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/userSpace/totalSpace")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content((new BigDecimal(1024)).toString().getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserTotalSpace404() throws Exception {
        Status status = new Status();
        status.setCode(404);
        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
        final String fakeSearchedUserId = "fakeSearchedUserId";
        doThrow(new UnknownUserException()).when(service).setSpaceOnBehalfOf(anyString(), anyLong(), anyString());

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/userSpace/totalSpace")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content((new BigDecimal(1024)).toString().getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserTotalSpace500() throws Exception {
        Status status = new Status();
        status.setCode(500);
        final String userCredentialsInAuthorizationHeader = "username:password";
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
        final String fakeSearchedUserId = "fakeSearchedUserId";
        doThrow(new NullPointerException()).when(service).setSpaceOnBehalfOf(anyString(), anyLong(), anyString());

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo/userSpace/totalSpace")
                        .contentType(MediaType.TEXT_PLAIN)
                        .characterEncoding("utf-8")
                        .content((new BigDecimal(1024)).toString().getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }
}