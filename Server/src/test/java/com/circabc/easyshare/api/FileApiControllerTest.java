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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.circabc.easyshare.api.TestHelper;

import static org.hamcrest.Matchers.containsString;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;

import com.circabc.easyshare.exceptions.CouldNotAllocateFileException;
import com.circabc.easyshare.exceptions.DateLiesInPastException;
import com.circabc.easyshare.exceptions.IllegalFileSizeException;
import com.circabc.easyshare.exceptions.UnknownFileException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongAuthenticationException;
import com.circabc.easyshare.exceptions.WrongPasswordException;
import com.circabc.easyshare.model.Credentials;
import com.circabc.easyshare.model.FileInfoRecipient;
import com.circabc.easyshare.model.FileInfoUploader;
import com.circabc.easyshare.model.FileRequest;
import com.circabc.easyshare.model.Recipient;
import com.circabc.easyshare.model.Status;
import com.circabc.easyshare.services.FileService;
import com.circabc.easyshare.services.UserService;
import com.circabc.easyshare.services.FileService.DownloadReturn;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.jni.FileInfo;
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
@WebMvcTest(FileApiController.class)
public class FileApiControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService service;

        @MockBean
        private FileService fileService;

        @Test
        public void deleteFile200() throws Exception {
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

                final String fakeSearchedFileId = "fakeSearchedUserId";

                doNothing().when(fileService).deleteFileOnBehalfOf(anyString(), anyString(), anyString());

                this.mockMvc.perform(MockMvcRequestBuilders.delete("/file/" + fakeSearchedFileId)
                                .param("reason", "fakeReason")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
        }

        @Test
        public void deleteFile403ForUserUnauthorized() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

                final String fakeSearchedFileId = "fakeSearchedUserId";

                doThrow(new UserUnauthorizedException()).when(fileService).deleteFileOnBehalfOf(anyString(),
                                anyString(), anyString());

                this.mockMvc.perform(MockMvcRequestBuilders.delete("/file/" + fakeSearchedFileId)
                                .param("reason", "fakeReason")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void deleteFile403ForNoAuthentication() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

                final String fakeSearchedFileId = "fakeSearchedUserId";

                doThrow(new UserUnauthorizedException()).when(fileService).deleteFileOnBehalfOf(anyString(),
                                anyString(), anyString());

                this.mockMvc.perform(MockMvcRequestBuilders.delete("/file/" + fakeSearchedFileId)
                                .param("reason", "fakeReason").accept(MediaType.APPLICATION_JSON)).andDo(print())
                                .andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void deleteFile403ForWrongAuthentication() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class)))
                                .thenThrow(new WrongAuthenticationException());
                final String fakeSearchedFileId = "fakeSearchedUserId";
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/file/" + fakeSearchedFileId)
                                .param("reason", "fakeReason")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void deleteFile404UnknownUser() throws Exception {
                Status status = new Status();
                status.setCode(404);
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new UnknownUserException()).when(fileService).deleteFileOnBehalfOf(anyString(), anyString(),
                                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/file/" + fakeSearchedFileId)
                                .param("reason", "fakeReason")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void deleteFile404UnknownFile() throws Exception {
                Status status = new Status();
                status.setCode(404);
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new UnknownFileException()).when(fileService).deleteFileOnBehalfOf(anyString(), anyString(),
                                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/file/" + fakeSearchedFileId)
                                .param("reason", "fakeReason")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void deleteFileSharedWithUser200() throws Exception {
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

                final String fakeSearchedFileId = "fakeSearchedUserId";
                final String fakeSearchedUserId = "fakeSearchedUserId";

                doNothing().when(fileService).deleteFileOnBehalfOf(anyString(), anyString(), anyString());

                this.mockMvc.perform(MockMvcRequestBuilders
                                .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId)
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
        }

        @Test
        public void deleteFileSharedWithUser403ForUserUnauthorized() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");

                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

                final String fakeSearchedFileId = "fakeSearchedUserId";
                final String fakeSearchedUserId = "fakeSearchedUserId";

                doThrow(new UserUnauthorizedException()).when(fileService).removeShareOnFileOnBehalfOf(anyString(),
                                anyString(), anyString());

                this.mockMvc.perform(MockMvcRequestBuilders
                                .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId)
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void deleteFileSharedWithUser403ForNoAuthentication() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                final String fakeSearchedUserId = "fakeSearchedUserId";
                this.mockMvc.perform(MockMvcRequestBuilders
                                .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId)
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void deleteFileSharedWithUser403ForWrongAuthentication() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");

                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class)))
                                .thenThrow(new WrongAuthenticationException());

                final String fakeSearchedFileId = "fakeSearchedUserId";
                final String fakeSearchedUserId = "fakeSearchedUserId";
                this.mockMvc.perform(MockMvcRequestBuilders
                                .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId)
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void deleteFileSharedWithUser404UnknownFile() throws Exception {
                Status status = new Status();
                status.setCode(404);
                status.setMessage("FileNotFound");

                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

                final String fakeSearchedFileId = "fakeSearchedUserId";
                final String fakeSearchedUserId = "fakeSearchedUserId";

                doThrow(new UnknownFileException()).when(fileService).removeShareOnFileOnBehalfOf(anyString(),
                                anyString(), anyString());
                this.mockMvc.perform(MockMvcRequestBuilders
                                .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId)
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void deleteFileSharedWithUser404UnknownUser() throws Exception {
                Status status = new Status();
                status.setCode(404);
                status.setMessage("UserNotFound");

                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

                final String fakeSearchedFileId = "fakeSearchedUserId";
                final String fakeSearchedUserId = "fakeSearchedUserId";

                doThrow(new UnknownUserException()).when(fileService).removeShareOnFileOnBehalfOf(anyString(),
                                anyString(), anyString());
                this.mockMvc.perform(MockMvcRequestBuilders
                                .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId)
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFile200() throws Exception {
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                File file = new File(getClass().getClassLoader().getResource("file.txt").getFile());
                doReturn(new DownloadReturn(file, "filename")).when(fileService).downloadOnBehalfOf(anyString(), anyString(),
                                anyString());

                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId)
                                .param("password", "fakePassword")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_OCTET_STREAM)).andDo(print()).andExpect(status().isOk())
                                .andExpect(content().bytes(FileUtils.readFileToByteArray(file)));
        }

        @Test
        public void getFile401() throws Exception {
                Status status = new Status();
                status.setCode(401);
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new WrongPasswordException()).when(fileService).downloadOnBehalfOf(anyString(), anyString(), anyString());

                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId)
                                .param("password", "WRONG_PASSWORD")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isUnauthorized())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFile403UserUnauthorized() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new UserUnauthorizedException()).when(fileService).downloadOnBehalfOf(anyString(), anyString(),
                                anyString());

                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId)
                                .param("password", "fakePassword")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFile403UserNoAuthentication() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId)
                                .param("password", "fakePassword").accept(MediaType.APPLICATION_JSON)).andDo(print())
                                .andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFile403WrongAuthentication() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class)))
                                .thenThrow(new WrongAuthenticationException());
                final String fakeSearchedFileId = "fakeSearchedUserId";
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId)
                                .param("password", "fakePassword")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFile404() throws Exception {
                Status status = new Status();
                status.setCode(404);
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new UnknownFileException()).when(fileService).downloadOnBehalfOf(anyString(), anyString(), anyString());

                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId)
                                .param("password", "fakePassword")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFile500() throws Exception {
                Status status = new Status();
                status.setCode(500);
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");

                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new UnknownUserException()).when(fileService).downloadOnBehalfOf(anyString(), anyString(), anyString());

                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId)
                                .param("password", "fakePassword")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFileFileInfoRecipient200() throws Exception {
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                FileInfoRecipient fileInfoRecipient = new FileInfoRecipient();
                fileInfoRecipient.setExpirationDate(LocalDate.now());
                fileInfoRecipient.setHasPassword(true);
                fileInfoRecipient.setName("name");
                fileInfoRecipient.setSize(new BigDecimal(4096));
                fileInfoRecipient.setUploader("uploader");
                doReturn(fileInfoRecipient).when(fileService).getFileInfoRecipientOnBehalfOf(anyString(), anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId + "/fileInfoRecipient")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
        }

        @Test
        public void getFileFileInfoRecipient403UserUnauthorized() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new UserUnauthorizedException()).when(fileService).getFileInfoRecipientOnBehalfOf(anyString(),
                                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId + "/fileInfoRecipient")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFileFileInfoRecipient403NoAuthentication() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new UserUnauthorizedException()).when(fileService).getFileInfoRecipientOnBehalfOf(anyString(),
                                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId + "/fileInfoRecipient")
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFileFileInfoRecipient403WrongAuthentication() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                when(service.getAuthenticatedUserId(any(Credentials.class)))
                                .thenThrow(new WrongAuthenticationException());
                final String fakeSearchedFileId = "fakeSearchedUserId";
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId + "/fileInfoRecipient")
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFileFileInfoRecipient404UnknownFile() throws Exception {
                Status status = new Status();
                status.setCode(404);
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new UnknownFileException()).when(fileService).getFileInfoRecipientOnBehalfOf(anyString(),
                                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId + "/fileInfoRecipient")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFileFileInfoRecipient500() throws Exception {
                Status status = new Status();
                status.setCode(500);
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new NullPointerException()).when(fileService).getFileInfoRecipientOnBehalfOf(anyString(),
                                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId + "/fileInfoRecipient")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFileFileInfoUploader200() throws Exception {
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                FileInfoUploader fileInfoUploader = new FileInfoUploader();
                fileInfoUploader.setExpirationDate(LocalDate.now());
                fileInfoUploader.setHasPassword(true);
                fileInfoUploader.setName("name");
                fileInfoUploader.setSize(new BigDecimal(4096));
                Recipient recipient = new Recipient();
                recipient.setEmailOrID("emailOrID");
                recipient.setMessage("message");
                Recipient[] sharedWithArray = new Recipient[] { recipient };
                fileInfoUploader.setSharedWith(Arrays.asList(sharedWithArray));
                doReturn(fileInfoUploader).when(fileService).getFileInfoUploaderOnBehalfOf(anyString(), anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId + "/fileInfoUploader")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
        }

        @Test
        public void getFileFileInfoUploader403UserUnauthorized() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new UserUnauthorizedException()).when(fileService).getFileInfoUploaderOnBehalfOf(anyString(),
                                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId + "/fileInfoUploader")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFileFileInfoUploader403NoAuthentication() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new UserUnauthorizedException()).when(fileService).getFileInfoUploaderOnBehalfOf(anyString(),
                                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId + "/fileInfoUploader")
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFileFileInfoUploader403WrongAuthentication() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                when(service.getAuthenticatedUserId(any(Credentials.class)))
                                .thenThrow(new WrongAuthenticationException());
                final String fakeSearchedFileId = "fakeSearchedUserId";
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId + "/fileInfoUploader")
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFileFileInfoUploader404UnknownFile() throws Exception {
                Status status = new Status();
                status.setCode(404);
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new UnknownFileException()).when(fileService).getFileInfoUploaderOnBehalfOf(anyString(),
                                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId + "/fileInfoUploader")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void getFileFileInfoUploader500() throws Exception {
                Status status = new Status();
                status.setCode(500);
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                final String fakeSearchedFileId = "fakeSearchedUserId";
                doThrow(new NullPointerException()).when(fileService).getFileInfoUploaderOnBehalfOf(anyString(),
                                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId + "/fileInfoUploader")
                                .header("Authorization",
                                                "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))
                                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void postFileFileRequest200() throws Exception {
                final String content = "{" + "\"expirationDate\": \"2019-04-16\"," + "\"hasPassword\": true,"
                                + "\"name\": \"string\"," + "\"size\": 0," + "\"password\": \"string\","
                                + "\"sharedWith\": [" + "{" + "\"emailOrID\": \"string\"," + "\"message\": \"string\""
                                + "}" + "]" + "}";
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                String fileId = "fileID";

                //(LocalDate expirationDate, String fileName, String password, String uploaderId, List<Recipient> recipientList, long filesize, String requesterId)
                doReturn(fileId).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class), anyString(), anyString(), anyString(), anyList(),
                                anyLong(), anyString());
                this.mockMvc.perform(
                                MockMvcRequestBuilders.post("/file/fileRequest")
                                                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))

                                                .content(content).contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.TEXT_PLAIN))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(content().string(containsString(fileId)));
        }

        @Test
        public void postFileFileRequest400() throws Exception {
                Status status = new Status();
                status.setCode(400);
                final String content = "{" + "\"expirationDate\": \"2019-0-16\"," + "\"hasPassword\": true,"
                                + "\"name\": \"string\"," + "\"size\": 0," + "\"password\": \"\","
                                + "\"sharedWith\": [" + "{" + "\"emailOrID\": \"string\"," + "\"message\": \"string\""
                                + "}" + "]" + "}";
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                doReturn("fileID").when(fileService).allocateFileOnBehalfOf(any(LocalDate.class), anyString(), anyString(), anyString(), anyList(),
                anyLong(), anyString());
                this.mockMvc.perform(
                                MockMvcRequestBuilders.post("/file/fileRequest")
                                                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))

                                                .content(content).contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.TEXT_PLAIN))
                                .andDo(print()).andExpect(status().isBadRequest())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));

                final String content2 = "{" + "\"expirationDate\": \"2019-04-16\"," + "\"hasPassword\": true,"
                                + "\"size\": 0," + "\"password\": \"\","
                                + "\"sharedWith\": [" + "{" + "\"emailOrID\": \"string\"," + "\"message\": \"string\""
                                + "}" + "]" + "}";
                        this.mockMvc.perform(
                                        MockMvcRequestBuilders.post("/file/fileRequest")
                                                        .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                                                                        userCredentialsInAuthorizationHeader.getBytes()))
                                                        .content(content2).contentType(MediaType.APPLICATION_JSON)
                                                        .accept(MediaType.TEXT_PLAIN))
                                        .andDo(print()).andExpect(status().isBadRequest())
                                        .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
                
        }

        @Test
        public void postFileFileRequest403IllegalFileSize() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                final String content = "{" + "\"expirationDate\": \"2019-04-16\"," + "\"hasPassword\": true,"
                                + "\"name\": \"string\"," + "\"size\": 0," + "\"password\": \"string\","
                                + "\"sharedWith\": [" + "{" + "\"emailOrID\": \"string\"," + "\"message\": \"string\""
                                + "}" + "]" + "}";
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                doThrow(new IllegalFileSizeException()).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class), anyString(), anyString(), anyString(), anyList(),
                anyLong(), anyString());
                this.mockMvc.perform(
                                MockMvcRequestBuilders.post("/file/fileRequest")
                                                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))

                                                .content(content).contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.TEXT_PLAIN))
                                .andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void postFileFileRequest403DateLiesInThePast() throws Exception {
                Status status = new Status();
                status.setCode(403);
                status.setMessage("NotAuthorized");
                final String content = "{" + "\"expirationDate\": \"2019-04-16\"," + "\"hasPassword\": true,"
                                + "\"name\": \"string\"," + "\"size\": 0," + "\"password\": \"string\","
                                + "\"sharedWith\": [" + "{" + "\"emailOrID\": \"string\"," + "\"message\": \"string\""
                                + "}" + "]" + "}";
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                doThrow(new DateLiesInPastException()).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class), anyString(), anyString(), anyString(), anyList(),
                anyLong(), anyString());
                this.mockMvc.perform(
                                MockMvcRequestBuilders.post("/file/fileRequest")
                                                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))

                                                .content(content).contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.TEXT_PLAIN))
                                .andDo(print()).andExpect(status().isForbidden())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void postFileFileRequest404UnknownUser() throws Exception {
                Status status = new Status();
                status.setCode(404);
                final String content = "{" + "\"expirationDate\": \"2019-04-16\"," + "\"hasPassword\": true,"
                                + "\"name\": \"string\"," + "\"size\": 0," + "\"password\": \"string\","
                                + "\"sharedWith\": [" + "{" + "\"emailOrID\": \"string\"," + "\"message\": \"string\""
                                + "}" + "]" + "}";
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                doThrow(new UnknownUserException()).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class), anyString(), anyString(), anyString(), anyList(),
                anyLong(), anyString());
                this.mockMvc.perform(
                                MockMvcRequestBuilders.post("/file/fileRequest")
                                                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))

                                                .content(content).contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.TEXT_PLAIN))
                                .andDo(print()).andExpect(status().isNotFound())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

        @Test
        public void postFileFileRequest500() throws Exception {
                Status status = new Status();
                status.setCode(500);
                final String content = "{" + "\"expirationDate\": \"2019-04-16\"," + "\"hasPassword\": true,"
                                + "\"name\": \"string\"," + "\"size\": 0," + "\"password\": \"string\","
                                + "\"sharedWith\": [" + "{" + "\"emailOrID\": \"string\"," + "\"message\": \"string\""
                                + "}" + "]" + "}";
                final String userCredentialsInAuthorizationHeader = "username:password";
                when(service.getAuthenticatedUserId(any(Credentials.class))).thenReturn("fakeAuthenticatedUserId");
                doThrow(new CouldNotAllocateFileException()).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class), anyString(), anyString(), anyString(), anyList(),
                anyLong(), anyString());
                this.mockMvc.perform(
                                MockMvcRequestBuilders.post("/file/fileRequest")
                                                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                                                                userCredentialsInAuthorizationHeader.getBytes()))

                                                .content(content).contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.TEXT_PLAIN))
                                .andDo(print()).andExpect(status().isInternalServerError())
                                .andExpect(content().string(containsString(TestHelper.asJsonString(status))));
        }

}