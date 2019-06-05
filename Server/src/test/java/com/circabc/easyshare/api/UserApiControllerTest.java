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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.model.Credentials;
import com.circabc.easyshare.model.FileInfoRecipient;
import com.circabc.easyshare.model.FileInfoUploader;
import com.circabc.easyshare.model.RecipientWithLink;
import com.circabc.easyshare.model.Status;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.services.FileService;
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
@WebMvcTest(UserApiController.class)
public class UserApiControllerTest {
    final String fakeSearchedUserId = "fakeSearchedUserId";
    final String userCredentialsInAuthorizationHeader = "username:password";
    final String fakeAuthenticatedUserId = "fakeAuthenticatedUserId";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @MockBean
    private FileService fileService;

    @Test
    public void getFilesFileInfoRecipient200() throws Exception {//NOSONAR
        FileInfoRecipient fileInfoRecipient = new FileInfoRecipient();
        fileInfoRecipient.setExpirationDate(LocalDate.now());
        fileInfoRecipient.setHasPassword(true);
        fileInfoRecipient.setName("fileName");
        fileInfoRecipient.setSize(new BigDecimal(1024));
        fileInfoRecipient.setUploaderName("uploaderName");
        List<FileInfoRecipient> fakeFileInfoRecipientList = Arrays.asList(fileInfoRecipient);

        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        when(fileService.getFileInfoRecipientOnBehalfOf(anyInt(), anyInt(), anyString(), anyString())).thenReturn(fakeFileInfoRecipientList);
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient").header("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(containsString(TestHelper.asJsonString(fakeFileInfoRecipientList))));
    }

    @Test
    public void getFilesFileInfoRecipient400() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(400);
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient").header("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getFilesFileInfoRecipient401ForNoAuthentication() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(401);
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient")
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getFilesFileInfoRecipient401ForWrongAuthentication() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(401);
        when(service.getAuthenticatedUserId(any(Credentials.class)))
                                .thenThrow(new WrongAuthenticationException());
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient").header("Authorization",
        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getFilesFileInfoRecipient403ForUnauthorizedUser() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");

        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        when(fileService.getFileInfoRecipientOnBehalfOf(anyInt(), anyInt(), anyString(), anyString())).thenThrow(new UserUnauthorizedException());
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient").header("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isForbidden())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getFilesFileInfoRecipient500() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(500);
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        when(fileService.getFileInfoRecipientOnBehalfOf(anyInt(), anyInt(), anyString(), anyString())).thenThrow(new NullPointerException());
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient").header("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isInternalServerError())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getFilesFileInfoUploader200() throws Exception { //NOSONAR
        FileInfoUploader fileInfoUploader = new FileInfoUploader();
        fileInfoUploader.setExpirationDate(LocalDate.now());
        fileInfoUploader.setHasPassword(true);
        fileInfoUploader.setName("fileName");
        fileInfoUploader.setSize(new BigDecimal(1024));
        fileInfoUploader.setFileId("fileId");

        RecipientWithLink recipientWithLink = new RecipientWithLink();
        recipientWithLink.setEmailOrName("email@email.com");
        recipientWithLink.setMessage("message");
        recipientWithLink.setSendEmail(true);
        recipientWithLink.setRecipientId("recipientId");
        recipientWithLink.setDownloadLink("fileId");

        fileInfoUploader.setSharedWith(Arrays.asList(recipientWithLink));

        List<FileInfoUploader> fakeFileInfoUploaderList = Arrays.asList(fileInfoUploader);

        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        when(fileService.getFileInfoUploaderOnBehalfOf(anyInt(), anyInt(), anyString(), anyString())).thenReturn(fakeFileInfoUploaderList);
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader").header("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(containsString(TestHelper.asJsonString(fakeFileInfoUploaderList))));
    }

    @Test
    public void getFilesFileInfoUploader400() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(400);
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader").header("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getFilesFileInfoUploader401ForNoAuthentication() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(401);
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader")
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getFilesFileInfoUploader401ForWrongAuthentication() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(401);
        when(service.getAuthenticatedUserId(any(Credentials.class)))
                                .thenThrow(new WrongAuthenticationException());
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader").header("Authorization",
        "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isUnauthorized())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getFilesFileInfoUploader403ForUnauthorizedUser() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");

        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        when(fileService.getFileInfoUploaderOnBehalfOf(anyInt(), anyInt(), anyString(), anyString())).thenThrow(new UserUnauthorizedException());
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader").header("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isForbidden())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void getFilesFileInfoUploader500() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(500);
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        when(fileService.getFileInfoUploaderOnBehalfOf(anyInt(), anyInt(), anyString(), anyString())).thenThrow(new NullPointerException());
        this.mockMvc
        .perform(MockMvcRequestBuilders.get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader").header("Authorization",
                "Basic " + Base64.getEncoder().encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                .param("pageSize", "10")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isInternalServerError())
        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserUserInfo200() throws Exception {//NOSONAR
        UserInfo userInfo = new UserInfo();
        userInfo.setId("id");
        userInfo.setIsAdmin(true);
        userInfo.setName("name");
        userInfo.setTotalSpace(new BigDecimal(1024));
        userInfo.setUsedSpace(new BigDecimal(0));
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        when(service.setUserInfoOnBehalfOf(any(UserInfo.class), anyString())).thenReturn(userInfo);
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(TestHelper.asJsonString(userInfo).getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(TestHelper.asJsonString(userInfo))));
    }

    @Test
    public void putUserUserInfo400() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(400);

        UserInfo userInfo = new UserInfo();
        userInfo.setId("id");
        userInfo.setIsAdmin(true);
        userInfo.setName("name");
        // missing total space
        userInfo.setUsedSpace(new BigDecimal(0));
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        when(service.setUserInfoOnBehalfOf(any(UserInfo.class), anyString())).thenReturn(userInfo);
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(TestHelper.asJsonString(userInfo).getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserUserInfo401ForNoAuthentication() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(401);

        UserInfo userInfo = new UserInfo();
        userInfo.setId("id");
        userInfo.setIsAdmin(true);
        userInfo.setName("name");
        userInfo.setTotalSpace(new BigDecimal(1024));
        userInfo.setUsedSpace(new BigDecimal(0));
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(TestHelper.asJsonString(userInfo).getBytes("utf-8"))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserUserInfo401ForWrongAuthentication() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(401);

        UserInfo userInfo = new UserInfo();
        userInfo.setId("id");
        userInfo.setIsAdmin(true);
        userInfo.setName("name");
        userInfo.setTotalSpace(new BigDecimal(1024));
        userInfo.setUsedSpace(new BigDecimal(0));
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenThrow(new WrongAuthenticationException());
        when(service.setUserInfoOnBehalfOf(any(UserInfo.class), anyString())).thenReturn(userInfo);
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(TestHelper.asJsonString(userInfo).getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserUserInfo403() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");

        UserInfo userInfo = new UserInfo();
        userInfo.setId("id");
        userInfo.setIsAdmin(true);
        userInfo.setName("name");
        userInfo.setTotalSpace(new BigDecimal(1024));
        userInfo.setUsedSpace(new BigDecimal(0));
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        when(service.setUserInfoOnBehalfOf(any(UserInfo.class), anyString())).thenThrow(new UserUnauthorizedException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(TestHelper.asJsonString(userInfo).getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserUserInfo404() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(404);

        UserInfo userInfo = new UserInfo();
        userInfo.setId("id");
        userInfo.setIsAdmin(true);
        userInfo.setName("name");
        userInfo.setTotalSpace(new BigDecimal(1024));
        userInfo.setUsedSpace(new BigDecimal(0));
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        when(service.setUserInfoOnBehalfOf(any(UserInfo.class), anyString())).thenThrow(new UnknownUserException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(TestHelper.asJsonString(userInfo).getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }

    @Test
    public void putUserUserInfo500() throws Exception {//NOSONAR
        Status status = new Status();
        status.setCode(500);

        UserInfo userInfo = new UserInfo();
        userInfo.setId("id");
        userInfo.setIsAdmin(true);
        userInfo.setName("name");
        userInfo.setTotalSpace(new BigDecimal(1024));
        userInfo.setUsedSpace(new BigDecimal(0));
        when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn(fakeAuthenticatedUserId);
        when(service.setUserInfoOnBehalfOf(any(UserInfo.class), anyString())).thenThrow(new NullPointerException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/user/" + fakeSearchedUserId + "/userInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(TestHelper.asJsonString(userInfo).getBytes("utf-8"))
                        .header("Authorization",
                                "Basic " + Base64.getEncoder()
                                        .encodeToString(userCredentialsInAuthorizationHeader.getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
    }
}