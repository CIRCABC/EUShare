/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package com.circabc.easyshare.integration;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.circabc.easyshare.error.HttpErrorAnswerBuilder;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.storage.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class LoginApiControllerITest {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private UserRepository userRepository;

  @MockBean
  private OpaqueTokenIntrospector opaqueTokenIntrospector;

  @LocalServerPort
  private int port;

  @Test
  public void postLogin200ITest() throws Exception {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    httpHeaders.setBearerAuth("token");
    HttpEntity httpEntity = new HttpEntity<String>("", httpHeaders);

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal("username",
        attributes, collection);
    when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);

    ResponseEntity<String> entity = this.testRestTemplate.postForEntity("/login", httpEntity, String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(userRepository.findOneByEmail("email@email.com").getId(), entity.getBody());
  }

  @Test
  public void postLogin401ITest() throws Exception {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    httpHeaders.setBearerAuth("token");
    HttpEntity httpEntity = new HttpEntity<String>("", httpHeaders);

    ResponseEntity<String> entity = this.testRestTemplate.postForEntity("/login", httpEntity, String.class);
    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  public void getUsersInfo200ITest() throws Exception {
    HttpEntity httpEntity = authenticateAsAdmin("");
    int pageSize = 1;
    int pageNumber = 0;
    String searchString = "email@email.com";
    UserInfo expectedUserInfo = userRepository.findOneByEmail("email@email.com").toUserInfo();
    UserInfo[] expectedUserInfos = { expectedUserInfo };

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}", HttpMethod.GET,
        httpEntity, String.class, pageSize, pageNumber, searchString);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(LoginApiControllerITest.asJsonString(expectedUserInfos), entity.getBody());
  }

  @Test
  public void getUsersInfo400ITest() throws Exception {
    HttpEntity httpEntity = authenticateAsAdmin("");

    int pageSize = 0;
    int pageNumber = 0;
    String searchString = "email@email.com";

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}", HttpMethod.GET,
        httpEntity, String.class, pageSize, pageNumber, searchString);
    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build400EmptyToString(), entity.getBody());
  }

  @Test
  public void getUsersInfo401ITest() throws Exception {
    int pageSize = 1;
    int pageNumber = 0;
    String searchString = "email@email.com";
    ResponseEntity<String> entity = this.testRestTemplate.withBasicAuth("admin", "wrongPassword").getForEntity(
        "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}", String.class,
        pageSize, pageNumber, searchString);
    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  public void getUsersInfo403ITest() throws Exception {
    int pageSize = 1;
    int pageNumber = 0;
    String searchString = "email@email.com";

    HttpEntity<String> httpEntity = this.authenticateAsInternalUser(searchString);

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}", HttpMethod.GET,
        httpEntity, String.class, pageSize, pageNumber, searchString);
    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build403NotAuthorizedToString(), entity.getBody());
  }

  @Test
  public void putUserInfo200ITest() throws Exception {
    UserInfo expectedUserInfo = userRepository.findOneByEmail("email@email.com").toUserInfo();
    expectedUserInfo.setIsAdmin(true);
    HttpEntity<String> httpEntity = this.authenticateAsAdmin(LoginApiControllerITest.asJsonString(expectedUserInfo));
    ResponseEntity<String> entity = this.testRestTemplate.exchange("/user/" + expectedUserInfo.getId() + "/userInfo",
        HttpMethod.PUT, httpEntity, String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(LoginApiControllerITest.asJsonString(expectedUserInfo), entity.getBody());
  }

  @Test
  public void getUserInfo200ITest() throws Exception {
    UserInfo expectedUserInfo = userRepository.findOneByEmail("email@email.com").toUserInfo();
    HttpEntity<String> httpEntity = this.authenticateAsAdmin(LoginApiControllerITest.asJsonString(expectedUserInfo));

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/user/" + expectedUserInfo.getId() + "/userInfo",
        HttpMethod.GET, httpEntity, String.class);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(LoginApiControllerITest.asJsonString(expectedUserInfo), entity.getBody());
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

  public HttpEntity<String> authenticateAsAdmin(String body) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    httpHeaders.setBearerAuth("token");
    HttpEntity<String> httpEntity = new HttpEntity<String>(body, httpHeaders);

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "admin@admin.com");
    attributes.put("username", "admin");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ADMIN");
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal("admin",
        attributes, collection);
    when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
    return httpEntity;
  }

  public HttpEntity<String> authenticateAsInternalUser(String body) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    httpHeaders.setBearerAuth("token");
    HttpEntity httpEntity = new HttpEntity<String>(body, httpHeaders);

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", "email@email.com");
    attributes.put("username", "username");
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal("username",
        attributes, collection);
    when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
    return httpEntity;
  }

}