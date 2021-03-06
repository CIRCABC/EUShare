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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.circabc.easyshare.exceptions.CouldNotAllocateFileException;
import com.circabc.easyshare.exceptions.DateLiesInPastException;
import com.circabc.easyshare.exceptions.EmptyFilenameException;
import com.circabc.easyshare.exceptions.FileLargerThanAllocationException;
import com.circabc.easyshare.exceptions.IllegalFileSizeException;
import com.circabc.easyshare.exceptions.UnknownFileException;
import com.circabc.easyshare.exceptions.UnknownUserException;
import com.circabc.easyshare.exceptions.UserHasInsufficientSpaceException;
import com.circabc.easyshare.exceptions.UserUnauthorizedException;
import com.circabc.easyshare.exceptions.WrongEmailStructureException;
import com.circabc.easyshare.exceptions.WrongPasswordException;
import com.circabc.easyshare.model.FileInfoUploader;
import com.circabc.easyshare.model.FileRequest;
import com.circabc.easyshare.model.Recipient;
import com.circabc.easyshare.model.RecipientWithLink;
import com.circabc.easyshare.model.Status;
import com.circabc.easyshare.services.FileService;
import com.circabc.easyshare.services.FileService.DownloadReturn;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.circabc.easyshare.services.UserService;
import com.google.common.io.Files;
import com.google.common.net.HttpHeaders;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
import org.springframework.web.multipart.MultipartFile;

import edu.emory.mathcs.backport.java.util.Collections;
import edu.emory.mathcs.backport.java.util.LinkedList;

@RunWith(SpringRunner.class)
@WebMvcTest(FileApiController.class)
public class FileApiControllerTest {
    final String fakeAuthenticatedUserId = "fakeAuthenticatedUserId";
    final String fakeSearchedFileId = "fakeSearchedFileId";
    final String fakeSearchedUserId = "fakeSearchedUserId";
    final String fileID = "fakeFileID";

    static String validFileRequestContent;
    static String validRecipient;
    static byte[] validFileContent;

    @BeforeClass
    public static void setup() throws IOException {
        FileRequest fileRequest = new FileRequest();
        fileRequest.setExpirationDate(LocalDate.now());
        fileRequest.setHasPassword(true);
        fileRequest.setPassword("password"); // NOSONAR
        fileRequest.setName("name");
        fileRequest.setSize(new BigDecimal(1024));
        Recipient recipient = new Recipient();
        recipient.setEmailOrName("email@email.com");
        recipient.setMessage("message");
        recipient.setSendEmail(true);
        validRecipient = FileApiControllerTest.asJsonString(recipient);
        fileRequest.setSharedWith(Arrays.asList(recipient));
        validFileRequestContent = FileApiControllerTest.asJsonString(fileRequest);
        ClassLoader classLoader = FileApiControllerTest.class.getClassLoader();
        File file = new File(classLoader.getResource("file.txt").getFile());
        validFileContent = Files.toByteArray(file);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @MockBean
    private FileService fileService;

    @MockBean
    private OpaqueTokenIntrospector opaqueTokenIntrospector;

    @Test
    public void deleteFile200() throws Exception { // NOSONAR
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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doNothing().when(fileService).deleteFileOnBehalfOf(anyString(), anyString(), anyString());
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/file/" + fakeSearchedFileId) // NOSONAR
                .param("reason", "fakeReason") // NOSONAR
                .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteFile401ForNoAuthentication() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(401);

        UserDetails userDetails = new User("username", "password", Collections.emptySet());
        when(service.loadUserByUsername(anyString())).thenReturn(userDetails);

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/file/" + fakeSearchedFileId).param("reason", "fakeReason")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void deleteFile401ForWrongAuthentication() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(401);

        String token = "StupidToken";
        when(opaqueTokenIntrospector.introspect(anyString())).thenThrow(new OAuth2IntrospectionException(""));

        UserDetails userDetails = new User("username", "password", Collections.emptySet());
        when(service.loadUserByUsername(anyString())).thenReturn(userDetails);

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/file/" + fakeSearchedFileId).param("reason", "fakeReason")
                        .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void deleteFile403ForUserUnauthorized() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized"); // NOSONAR

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new UserUnauthorizedException()).when(fileService).deleteFileOnBehalfOf(anyString(), anyString(),
                anyString());

        this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/file/" + fakeSearchedFileId).param("reason", "fakeReason")
                        .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void deleteFile404() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(404);

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new UnknownFileException()).when(fileService).deleteFileOnBehalfOf(anyString(), anyString(),
                anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/file/" + fakeSearchedFileId).param("reason", "fakeReason")
                        .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void deleteFileSharedWithUser200() throws Exception {// NOSONAR
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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doNothing().when(fileService).deleteFileOnBehalfOf(anyString(), anyString(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId) // NOSONAR
                        .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void deleteFileSharedWithUser403ForUserUnauthorized() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new UserUnauthorizedException()).when(fileService).removeShareOnFileOnBehalfOf(anyString(), anyString(),
                anyString());

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId)
                        .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void deleteFileSharedWithUser401ForNoAuthentication() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(401);

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void deleteFileSharedWithUser401ForWrongAuthentication() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(401);

        String token = "StupidToken";
        when(opaqueTokenIntrospector.introspect(anyString())).thenThrow(new OAuth2IntrospectionException(""));

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId)
                        .header("Authorization", "Bearer " + token))
                .andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void deleteFileSharedWithUser404UnknownFile() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(404);
        status.setMessage("FileNotFound");

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new UnknownFileException()).when(fileService).removeShareOnFileOnBehalfOf(anyString(), anyString(),
                anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId)
                        .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void deleteFileSharedWithUser404UnknownUser() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(404);
        status.setMessage("UserNotFound");

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new UnknownUserException()).when(fileService).removeShareOnFileOnBehalfOf(anyString(), anyString(),
                anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId)
                        .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void deleteFileSharedWithUser500() throws Exception {// NOSONAR
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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new NullPointerException()).when(fileService).removeShareOnFileOnBehalfOf(anyString(), anyString(),
                anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith/" + fakeSearchedUserId)
                        .header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void getFile200() throws Exception { // NOSONAR
        File file = new File(getClass().getClassLoader().getResource("file.txt").getFile());

        doReturn(new DownloadReturn(file, "filename", 256L)).when(fileService).downloadFile(anyString(), anyString());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId)// NOSONAR
                .param("password", "fakePassword") // NOSONAR
                .accept(MediaType.APPLICATION_OCTET_STREAM)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().bytes(FileUtils.readFileToByteArray(file)))
                .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, 256L));
    }

    @Test
    public void getFile401() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(401);
        doThrow(new WrongPasswordException()).when(fileService).downloadFile(anyString(), anyString());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId) // NOSONAR
                .param("password", "WRONG_PASSWORD") // NOSONAR
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void getFile404() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(404);
        doThrow(new UnknownFileException()).when(fileService).downloadFile(anyString(), anyString());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId)// NOSONAR
                .param("password", "fakePassword")// NOSONAR
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void getFile500() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(500);
        doThrow(new NullPointerException()).when(fileService).downloadFile(anyString(), anyString());
        this.mockMvc.perform(MockMvcRequestBuilders.get("/file/" + fakeSearchedFileId)// NOSONAR
                .param("password", "fakePassword")// NOSONAR
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileRequest200() throws Exception {// NOSONAR
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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doReturn(fileID).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class), anyString(), anyString(),
                anyString(), anyList(), anyLong(), anyString());
        this.mockMvc.perform(MockMvcRequestBuilders.post("/file/fileRequest") // NOSONAR
                .header("Authorization", "Bearer " + token).characterEncoding("utf-8").content(validFileRequestContent)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN)).andDo(print())
                .andExpect(status().isOk()).andExpect(content().string(containsString(fileID)));
    }

    @Test
    public void postFileFileRequest400() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(400);
        final String content = "{" + "\"expirationDate\": \"2019-0-16\"," + "\"hasPassword\": true,"
                + "\"name\": \"string\"," + "\"size\": 0," + "\"password\": \"\"," + "\"sharedWith\": [" + "{"
                + "\"emailOrID\": \"string\"," + "\"message\": \"string\"" + "\"sendEmail\": true" + "}" + "]" + "}";

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doReturn("fileID").when(fileService).allocateFileOnBehalfOf(any(LocalDate.class), anyString(), anyString(),
                anyString(), anyList(), anyLong(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/file/fileRequest").header("Authorization",
                        "Bearer " + token)
                        .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileRequest403NotAuthorized() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new UserUnauthorizedException()).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class),
                anyString(), anyString(), anyString(), anyList(), anyLong(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/file/fileRequest").header("Authorization",
                        "Bearer " + token)
                        .content(validFileRequestContent).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileRequest403IllegalFileSize() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("IllegalFileSize");

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new IllegalFileSizeException()).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class),
                anyString(), anyString(), anyString(), anyList(), anyLong(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/file/fileRequest").header("Authorization",
                        "Bearer " + token)
                        .content(validFileRequestContent).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileRequest403DateLiesInThePast() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("DateLiesInPast");

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new DateLiesInPastException()).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class),
                anyString(), anyString(), anyString(), anyList(), anyLong(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/file/fileRequest").header("Authorization",
                        "Bearer " + token)
                        .content(validFileRequestContent).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileRequest403UserHasInsufficientSpace() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("UserHasInsufficientSpace");

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new UserHasInsufficientSpaceException()).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class),
                anyString(), anyString(), anyString(), anyList(), anyLong(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/file/fileRequest").header("Authorization",
                        "Bearer " + token)
                        .content(validFileRequestContent).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileRequest403EmptyFileName() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("EmptyFileName");

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new EmptyFilenameException()).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class),
                anyString(), anyString(), anyString(), anyList(), anyLong(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/file/fileRequest").header("Authorization",
                        "Bearer " + token)
                        .content(validFileRequestContent).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileRequest403WrongEmailStructure() throws Exception { // NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("WrongEmailStructure");

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new WrongEmailStructureException()).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class),
                anyString(), anyString(), anyString(), anyList(), anyLong(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/file/fileRequest").header("Authorization",
                        "Bearer " + token)
                        .content(validFileRequestContent).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileRequest500() throws Exception { // NOSONAR
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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new CouldNotAllocateFileException()).when(fileService).allocateFileOnBehalfOf(any(LocalDate.class),
                anyString(), anyString(), anyString(), anyList(), anyLong(), anyString());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/file/fileRequest").header("Authorization",
                        "Bearer " + token)
                        .content(validFileRequestContent).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.TEXT_PLAIN))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileSharedWith200() throws Exception {// NOSONAR
        RecipientWithLink recipientWithLink = new RecipientWithLink();
        recipientWithLink.setEmailOrName("email@email.com");
        recipientWithLink.setMessage("message");
        recipientWithLink.setSendEmail(true);
        recipientWithLink.setRecipientId("recipientId");

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        when(fileService.addShareOnFileOnBehalfOf(anyString(), any(Recipient.class), anyString()))
                .thenReturn(recipientWithLink);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith") // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8").content(validRecipient).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void postFileSharedWith400() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(400);
        RecipientWithLink recipientWithLink = new RecipientWithLink();
        recipientWithLink.setEmailOrName("email@email.com");
        recipientWithLink.setMessage("message");
        recipientWithLink.setSendEmail(true);
        recipientWithLink.setRecipientId("recipientId");

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
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);


        when(fileService.addShareOnFileOnBehalfOf(anyString(), any(Recipient.class), anyString()))
                .thenReturn(recipientWithLink);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith") // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8").content(validRecipient).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_HTML)).andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileSharedWith401ForNoAuthentication() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(401);
        RecipientWithLink recipientWithLink = new RecipientWithLink();
        recipientWithLink.setEmailOrName("email@email.com");
        recipientWithLink.setMessage("message");
        recipientWithLink.setSendEmail(true);
        recipientWithLink.setRecipientId("recipientId");

        when(fileService.addShareOnFileOnBehalfOf(anyString(), any(Recipient.class), anyString()))
                .thenReturn(recipientWithLink);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith") // NOSONAR
                .characterEncoding("utf-8").content(validRecipient).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileSharedWith401ForWrongAuthentication() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(401);

        String token = "StupidToken";
        when(opaqueTokenIntrospector.introspect(anyString())).thenThrow(new OAuth2IntrospectionException(""));

        RecipientWithLink recipientWithLink = new RecipientWithLink();
        recipientWithLink.setEmailOrName("email@email.com");
        recipientWithLink.setMessage("message");
        recipientWithLink.setSendEmail(true);
        recipientWithLink.setRecipientId("recipientId");

        when(fileService.addShareOnFileOnBehalfOf(anyString(), any(Recipient.class), anyString()))
                .thenReturn(recipientWithLink);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith") // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8").content(validRecipient).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileSharedWith403() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");

        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new UserUnauthorizedException()).when(fileService).addShareOnFileOnBehalfOf(anyString(),
                any(Recipient.class), anyString());
        this.mockMvc.perform(MockMvcRequestBuilders.post("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith") // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8").content(validRecipient).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileSharedWith404ForUnknownUser() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(404);

        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new UnknownUserException()).when(fileService).addShareOnFileOnBehalfOf(anyString(),
                any(Recipient.class), anyString());
        this.mockMvc.perform(MockMvcRequestBuilders.post("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith") // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8").content(validRecipient).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileSharedWith404ForUnknownFile() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(404);

        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);


        doThrow(new UnknownFileException()).when(fileService).addShareOnFileOnBehalfOf(anyString(),
                any(Recipient.class), anyString());
        this.mockMvc.perform(MockMvcRequestBuilders.post("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith") // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8").content(validRecipient).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileSharedWith500() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(500);

        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new NullPointerException()).when(fileService).addShareOnFileOnBehalfOf(anyString(),
                any(Recipient.class), anyString());
        this.mockMvc.perform(MockMvcRequestBuilders.post("/file/" + fakeSearchedFileId + "/fileRequest/sharedWith") // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8").content(validRecipient).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileContent200() throws Exception {// NOSONAR
        FileInfoUploader fileInfoUploader = new FileInfoUploader();
        fileInfoUploader.setFileId("fileId");
        fileInfoUploader.setExpirationDate(LocalDate.now());
        fileInfoUploader.setHasPassword(false);
        fileInfoUploader.setName("name");
        fileInfoUploader.setSharedWith(new LinkedList());
        fileInfoUploader.setSize(new BigDecimal(1024));
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "name","multipart/form-data", validFileContent);

        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        when(fileService.saveOnBehalfOf(anyString(), any(MultipartFile.class), anyString())).thenReturn(fileInfoUploader);
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/file/" + fakeSearchedFileId + "/fileRequest/fileContent").file(mockMultipartFile) // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8").contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void postFileFileContent400() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(400);

        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        FileInfoUploader fileInfoUploader = new FileInfoUploader();
        fileInfoUploader.setFileId("fileId");
        fileInfoUploader.setExpirationDate(LocalDate.now());
        fileInfoUploader.setHasPassword(false);
        fileInfoUploader.setName("name");
        fileInfoUploader.setSharedWith(new LinkedList());
        fileInfoUploader.setSize(new BigDecimal(1024));
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "name","multipart/form-data", "".getBytes());

        UserDetails userDetails = new User("username", "password", Collections.emptySet());
        when(service.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        when(fileService.saveOnBehalfOf(anyString(), any(MultipartFile.class), anyString())).thenReturn(fileInfoUploader);
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/file/" + fakeSearchedFileId + "/fileRequest/fileContent").file(mockMultipartFile) // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8").contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileContent401ForNoAuthentication() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(401);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/file/" + fakeSearchedFileId + "/fileRequest/fileContent") // NOSONAR
                .characterEncoding("utf-8").content(validFileContent).contentType(MediaType.APPLICATION_OCTET_STREAM)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileContent401ForWrongAuthentication() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(401);

        String token = "StupidToken";

        FileInfoUploader fileInfoUploader = new FileInfoUploader();
        fileInfoUploader.setFileId("fileId");
        fileInfoUploader.setExpirationDate(LocalDate.now());
        fileInfoUploader.setHasPassword(false);
        fileInfoUploader.setName("name");
        fileInfoUploader.setSharedWith(new LinkedList());
        fileInfoUploader.setSize(new BigDecimal(1024));
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "name","multipart/form-data", validFileContent);

        when(opaqueTokenIntrospector.introspect(anyString())).thenThrow(new OAuth2IntrospectionException(""));

        when(fileService.saveOnBehalfOf(anyString(), any(MultipartFile.class), anyString())).thenReturn(fileInfoUploader);
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/file/" + fakeSearchedFileId + "/fileRequest/fileContent").file(mockMultipartFile) // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8").contentType(MediaType.APPLICATION_OCTET_STREAM)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileContent403() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("NotAuthorized");

        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "name","multipart/form-data", validFileContent);

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new UserUnauthorizedException()).when(fileService).saveOnBehalfOf(anyString(), any(MultipartFile.class),
                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/file/" + fakeSearchedFileId + "/fileRequest/fileContent").file(mockMultipartFile) // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8").contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileContent403FileLargerThanAllocation() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("FileLargerThanAllocation");

        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "name","multipart/form-data", validFileContent);

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new FileLargerThanAllocationException()).when(fileService).saveOnBehalfOf(anyString(),
                any(MultipartFile.class), anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/file/" + fakeSearchedFileId + "/fileRequest/fileContent").file(mockMultipartFile) // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    /**
     * File size is bigger than what the system allows in its properties
     */
    public void postFileFileContent403IllegalFileSize() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(403);
        status.setMessage("IllegalFileSize");

        String token = "StupidToken";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "name","multipart/form-data", validFileContent);

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new IllegalFileSizeException()).when(fileService).saveOnBehalfOf(anyString(), any(MultipartFile.class),
                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/file/" + fakeSearchedFileId + "/fileRequest/fileContent").file(mockMultipartFile) // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isForbidden())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileContent404() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(404);

        String token = "StupidToken";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "name","multipart/form-data", validFileContent);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new UnknownFileException()).when(fileService).saveOnBehalfOf(anyString(), any(MultipartFile.class),
                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/file/" + fakeSearchedFileId + "/fileRequest/fileContent").file(mockMultipartFile) // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
    }

    @Test
    public void postFileFileContent500() throws Exception {// NOSONAR
        Status status = new Status();
        status.setCode(500);

        String token = "StupidToken";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "name","multipart/form-data", validFileContent);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "email@email.com");
        attributes.put("username", "username");
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                        "username", attributes, collection);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
        when(service.getAuthenticatedUserId(any(Authentication.class))).thenReturn(fakeAuthenticatedUserId);

        doThrow(new NullPointerException()).when(fileService).saveOnBehalfOf(anyString(), any(MultipartFile.class),
                anyString());
                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/file/" + fakeSearchedFileId + "/fileRequest/fileContent").file(mockMultipartFile) // NOSONAR
                .header("Authorization",
                        "Bearer " + token)
                .characterEncoding("utf-8")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString(FileApiControllerTest.asJsonString(status))));
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