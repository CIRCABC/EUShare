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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.circabc.eushare.exceptions.UnknownUserException;
import eu.europa.circabc.eushare.exceptions.UserUnauthorizedException;
import eu.europa.circabc.eushare.model.FileInfoRecipient;
import eu.europa.circabc.eushare.model.FileInfoUploader;
import eu.europa.circabc.eushare.model.Recipient;
import eu.europa.circabc.eushare.model.Status;
import eu.europa.circabc.eushare.model.UserInfo;
import eu.europa.circabc.eushare.services.FileService;
import eu.europa.circabc.eushare.services.UserService;
import eu.europa.circabc.eushare.storage.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@ContextHierarchy({
  @ContextConfiguration(classes = UserRepository.class)
})
@WebMvcTest(UserApiController.class)
public class UserApiControllerTest {

  final String fakeSearchedUserId = "fakeSearchedUserId";
  final String userCredentialsInAuthorizationHeader = "username:password";
  final String fakeAuthenticatedUserId = "fakeAuthenticatedUserId";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private OpaqueTokenIntrospector opaqueTokenIntrospector;

  @MockBean
  private UserService service;

  @MockBean
  private FileService fileService;

  @Test
  public void getFilesFileInfoRecipient200() throws Exception { // NOSONAR
    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    FileInfoRecipient fileInfoRecipient = new FileInfoRecipient();
    fileInfoRecipient.setExpirationDate(LocalDate.now());
    fileInfoRecipient.setHasPassword(true);
    fileInfoRecipient.setName("fileName");
    fileInfoRecipient.setSize(new BigDecimal(1024));
    fileInfoRecipient.setUploaderName("uploaderName");

    List<FileInfoRecipient> fakeFileInfoRecipientList = Arrays.asList(
      fileInfoRecipient
    );
    when(
      fileService.getFileInfoRecipientOnBehalfOf(
        anyInt(),
        anyInt(),
        anyString(),
        anyString()
      )
    )
      .thenReturn(fakeFileInfoRecipientList);
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient")
          .header("Authorization", "Bearer " + token)
          .param("pageSize", "10")
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(
        content()
          .string(
            containsString(
              UserApiControllerTest.asJsonString(fakeFileInfoRecipientList)
            )
          )
      );
  }

  @Test
  public void getFilesFileInfoRecipient400() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(400);

    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient")
          .header("Authorization", "Bearer " + token)
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getFilesFileInfoRecipient401ForNoAuthentication()
    throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(401);

    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient")
          .param("pageSize", "10")
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isUnauthorized())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getFilesFileInfoRecipient401ForWrongAuthentication()
    throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(401);

    String token = "StupidToken";

    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenThrow(new OAuth2IntrospectionException(""));
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient")
          .header("Authorization", "Bearer " + token)
          .param("pageSize", "10")
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isUnauthorized())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getFilesFileInfoRecipient403ForUnauthorizedUser()
    throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(403);
    status.setMessage("NotAuthorized");

    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    when(
      fileService.getFileInfoRecipientOnBehalfOf(
        anyInt(),
        anyInt(),
        anyString(),
        anyString()
      )
    ) // pretending
      .thenThrow(new UserUnauthorizedException());
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient")
          .header("Authorization", "Bearer " + token)
          .param("pageSize", "10")
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isForbidden())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getFilesFileInfoRecipient404() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(404);

    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    when(
      fileService.getFileInfoRecipientOnBehalfOf(
        anyInt(),
        anyInt(),
        anyString(),
        anyString()
      )
    )
      .thenThrow(new UnknownUserException());
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient")
          .header("Authorization", "Bearer " + token)
          .param("pageSize", "10")
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getFilesFileInfoRecipient500() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(500);

    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    when(
      fileService.getFileInfoRecipientOnBehalfOf(
        anyInt(),
        anyInt(),
        anyString(),
        anyString()
      )
    )
      .thenThrow(new NullPointerException());
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoRecipient")
          .header("Authorization", "Bearer " + token)
          .param("pageSize", "10")
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isInternalServerError())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getFilesFileInfoUploader200() throws Exception { // NOSONAR
    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    FileInfoUploader fileInfoUploader = new FileInfoUploader();
    fileInfoUploader.setExpirationDate(LocalDate.now());
    fileInfoUploader.setHasPassword(true);
    fileInfoUploader.setName("fileName");
    fileInfoUploader.setSize(new BigDecimal(1024));
    fileInfoUploader.setFileId("fileId");

    Recipient recipientWithLink = new Recipient();
    recipientWithLink.setEmail("email@email.com");
    recipientWithLink.setMessage("message");
    recipientWithLink.setDownloadLink("fileId");

    fileInfoUploader.setSharedWith(Arrays.asList(recipientWithLink));

    List<FileInfoUploader> fakeFileInfoUploaderList = Arrays.asList(
      fileInfoUploader
    );

    when(
      fileService.getFileInfoUploaderOnBehalfOf(
        anyInt(),
        anyInt(),
        anyString(),
        anyString()
      )
    )
      .thenReturn(fakeFileInfoUploaderList);
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader")
          .header("Authorization", "Bearer " + token)
          .param("pageSize", "10")
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(
        content()
          .string(
            containsString(
              UserApiControllerTest.asJsonString(fakeFileInfoUploaderList)
            )
          )
      );
  }

  @Test
  public void getFilesFileInfoUploader400() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(400);

    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader")
          .header("Authorization", "Bearer " + token)
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getFilesFileInfoUploader401ForNoAuthentication()
    throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(401);
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader")
          .param("pageSize", "10")
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isUnauthorized())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getFilesFileInfoUploader401ForWrongAuthentication()
    throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(401);
    String token = "StupidToken";

    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenThrow(new OAuth2IntrospectionException(""));

    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader")
          .header("Authorization", "Bearer " + token)
          .param("pageSize", "10")
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isUnauthorized())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getFilesFileInfoUploader403ForUnauthorizedUser()
    throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(403);
    status.setMessage("NotAuthorized");

    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    when(
      fileService.getFileInfoUploaderOnBehalfOf(
        anyInt(),
        anyInt(),
        anyString(),
        anyString()
      )
    )
      .thenThrow(new UserUnauthorizedException());
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader")
          .header("Authorization", "Bearer " + token)
          .param("pageSize", "10")
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isForbidden())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getFilesFileInfoUploader404() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(404);

    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    when(
      fileService.getFileInfoUploaderOnBehalfOf(
        anyInt(),
        anyInt(),
        anyString(),
        anyString()
      )
    )
      .thenThrow(new UnknownUserException());
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader")
          .header("Authorization", "Bearer " + token)
          .param("pageSize", "10")
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getFilesFileInfoUploader500() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(500);

    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    when(
      fileService.getFileInfoUploaderOnBehalfOf(
        anyInt(),
        anyInt(),
        anyString(),
        anyString()
      )
    )
      .thenThrow(new NullPointerException());
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/files/fileInfoUploader")
          .header("Authorization", "Bearer " + token)
          .param("pageSize", "10")
          .param("pageNumber", "0")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isInternalServerError())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void putUserUserInfo200() throws Exception { // NOSONAR
    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "loginUsername");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    UserInfo userInfo = new UserInfo();
    userInfo.setId("id");
    userInfo.setIsAdmin(true);
    userInfo.setGivenName("name SURNAME");
    userInfo.setLoginUsername("loginUsername");
    userInfo.setEmail("email@email.com");
    userInfo.setTotalSpace(new BigDecimal(1024));
    userInfo.setUsedSpace(new BigDecimal(0));

    when(service.setUserInfoOnBehalfOf(any(UserInfo.class), anyString()))
      .thenReturn(userInfo);
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .put("/user/" + fakeSearchedUserId + "/userInfo")
          .contentType(MediaType.APPLICATION_JSON)
          .characterEncoding("utf-8")
          .content(
            UserApiControllerTest.asJsonString(userInfo).getBytes("utf-8")
          )
          .header("Authorization", "Bearer " + token)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(userInfo)))
      );
  }

  @Test
  public void putUserUserInfo400() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(400);

    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    UserInfo userInfo = new UserInfo();
    userInfo.setId("id");
    userInfo.setIsAdmin(true);
    userInfo.setGivenName("name SURNAME");
    userInfo.setLoginUsername("loginUsername");
    userInfo.setEmail("email@email.com");

    // missing total space
    userInfo.setUsedSpace(new BigDecimal(0));

    when(service.setUserInfoOnBehalfOf(any(UserInfo.class), anyString()))
      .thenReturn(userInfo);
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .put("/user/" + fakeSearchedUserId + "/userInfo")
          .contentType(MediaType.APPLICATION_JSON)
          .characterEncoding("utf-8")
          .content(
            UserApiControllerTest.asJsonString(userInfo).getBytes("utf-8")
          )
          .header("Authorization", "Bearer " + token)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void putUserUserInfo401ForNoAuthentication() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(401);

    UserInfo userInfo = new UserInfo();
    userInfo.setId("id");
    userInfo.setIsAdmin(true);
    userInfo.setGivenName("name SURNAME");
    userInfo.setLoginUsername("loginUsername");
    userInfo.setEmail("email@email.com");
    userInfo.setTotalSpace(new BigDecimal(1024));
    userInfo.setUsedSpace(new BigDecimal(0));
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .put("/user/" + fakeSearchedUserId + "/userInfo")
          .contentType(MediaType.APPLICATION_JSON)
          .characterEncoding("utf-8")
          .content(
            UserApiControllerTest.asJsonString(userInfo).getBytes("utf-8")
          )
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isUnauthorized())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void putUserUserInfo401ForWrongAuthentication() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(401);
    String token = "StupidToken";
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenThrow(new OAuth2IntrospectionException(""));

    UserInfo userInfo = new UserInfo();
    userInfo.setId("id");
    userInfo.setIsAdmin(true);
    userInfo.setGivenName("name SURNAME");
    userInfo.setLoginUsername("loginUsername");
    userInfo.setEmail("email@email.com");
    userInfo.setTotalSpace(new BigDecimal(1024));
    userInfo.setUsedSpace(new BigDecimal(0));

    when(service.setUserInfoOnBehalfOf(any(UserInfo.class), anyString()))
      .thenReturn(userInfo);
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .put("/user/" + fakeSearchedUserId + "/userInfo")
          .contentType(MediaType.APPLICATION_JSON)
          .characterEncoding("utf-8")
          .content(
            UserApiControllerTest.asJsonString(userInfo).getBytes("utf-8")
          )
          .header("Authorization", "Bearer " + token)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isUnauthorized())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void putUserUserInfo403() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(403);
    status.setMessage("NotAuthorized");

    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    UserInfo userInfo = new UserInfo();
    userInfo.setId("id");
    userInfo.setIsAdmin(true);
    userInfo.setGivenName("name SURNAME");
    userInfo.setLoginUsername("loginUsername");
    userInfo.setEmail("email@email.com");
    userInfo.setTotalSpace(new BigDecimal(1024));
    userInfo.setUsedSpace(new BigDecimal(0));

    when(service.setUserInfoOnBehalfOf(any(UserInfo.class), anyString()))
      .thenThrow(new UserUnauthorizedException());

    this.mockMvc.perform(
        MockMvcRequestBuilders
          .put("/user/" + fakeSearchedUserId + "/userInfo")
          .contentType(MediaType.APPLICATION_JSON)
          .characterEncoding("utf-8")
          .content(
            UserApiControllerTest.asJsonString(userInfo).getBytes("utf-8")
          )
          .header("Authorization", "Bearer " + token)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isForbidden())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void putUserUserInfo404() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(404);

    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    UserInfo userInfo = new UserInfo();
    userInfo.setId("id");
    userInfo.setIsAdmin(true);
    userInfo.setGivenName("name SURNAME");
    userInfo.setLoginUsername("loginUsername");
    userInfo.setEmail("email@email.com");
    userInfo.setTotalSpace(new BigDecimal(1024));
    userInfo.setUsedSpace(new BigDecimal(0));

    when(service.setUserInfoOnBehalfOf(any(UserInfo.class), anyString()))
      .thenThrow(new UnknownUserException());
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .put("/user/" + fakeSearchedUserId + "/userInfo")
          .contentType(MediaType.APPLICATION_JSON)
          .characterEncoding("utf-8")
          .content(
            UserApiControllerTest.asJsonString(userInfo).getBytes("utf-8")
          )
          .header("Authorization", "Bearer " + token)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void putUserUserInfo500() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(500);

    UserInfo userInfo = new UserInfo();
    userInfo.setId("id");
    userInfo.setIsAdmin(true);
    userInfo.setGivenName("name SURNAME");
    userInfo.setLoginUsername("loginUsername");
    userInfo.setEmail("email@email.com");
    userInfo.setTotalSpace(new BigDecimal(1024));
    userInfo.setUsedSpace(new BigDecimal(0));

    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    when(service.setUserInfoOnBehalfOf(any(UserInfo.class), anyString()))
      .thenThrow(new NullPointerException());
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .put("/user/" + fakeSearchedUserId + "/userInfo")
          .contentType(MediaType.APPLICATION_JSON)
          .characterEncoding("utf-8")
          .content(
            UserApiControllerTest.asJsonString(userInfo).getBytes("utf-8")
          )
          .header("Authorization", "Bearer " + token)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isInternalServerError())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getUserInfo200() throws Exception { // NOSONAR
    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);

    UserInfo fakeUserInfo = new UserInfo();
    fakeUserInfo.setId("id");
    fakeUserInfo.setIsAdmin(false);
    fakeUserInfo.setTotalSpace(new BigDecimal(1024 * 1204));
    fakeUserInfo.setUsedSpace(new BigDecimal(0));
    when(service.getUserInfoOnBehalfOf(anyString(), anyString()))
      .thenReturn(fakeUserInfo);
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/userInfo")
          .header("Authorization", "Bearer " + token)
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(
        content()
          .string(
            containsString(UserApiControllerTest.asJsonString(fakeUserInfo))
          )
      );
  }

  @Test
  public void getUserInfo401ForWrongAuthentication() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(401);
    String token = "StupidToken";
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenThrow(new OAuth2IntrospectionException(""));
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/userInfo")
          .header("Authorization", "Bearer " + token)
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isUnauthorized())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getUserInfo401ForNoAuthentication() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(401);
    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/userInfo")
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isUnauthorized())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getUserInfo403() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(403);
    status.setMessage("NotAuthorized");
    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);
    when(service.getUserInfoOnBehalfOf(anyString(), anyString()))
      .thenThrow(new UserUnauthorizedException());

    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/userInfo")
          .header("Authorization", "Bearer " + token)
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isForbidden())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getUserInfo404() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(404);
    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenReturn(fakeAuthenticatedUserId);
    when(service.getUserInfoOnBehalfOf(anyString(), anyString()))
      .thenThrow(new UnknownUserException());

    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/userInfo")
          .header("Authorization", "Bearer " + token)
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  @Test
  public void getUserInfo500() throws Exception { // NOSONAR
    Status status = new Status();
    status.setCode(500);
    String token = "StupidToken";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("name", "name SURNAME");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
      "INTERNAL"
    );
    Collection<GrantedAuthority> collection = new LinkedList();
    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
      "username",
      attributes,
      collection
    );
    when(opaqueTokenIntrospector.introspect(anyString()))
      .thenReturn(oAuth2AuthenticatedPrincipal);
    when(service.getAuthenticatedUserId(any(Authentication.class)))
      .thenThrow(new NullPointerException());

    this.mockMvc.perform(
        MockMvcRequestBuilders
          .get("/user/" + fakeSearchedUserId + "/userInfo")
          .header("Authorization", "Bearer " + token)
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
      )
      .andDo(print())
      .andExpect(status().isInternalServerError())
      .andExpect(
        content()
          .string(containsString(UserApiControllerTest.asJsonString(status)))
      );
  }

  public static String asJsonString(final Object obj) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule(new JavaTimeModule());
      mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      final String jsonContent = mapper.writeValueAsString(obj);
      return jsonContent;
    } catch (Exception e) {
      throw new RuntimeException(e); // NOSONAR
    }
  }
}
