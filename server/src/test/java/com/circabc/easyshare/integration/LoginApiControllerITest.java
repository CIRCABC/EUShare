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

import java.util.Base64;

import com.circabc.easyshare.TestHelper;
import com.circabc.easyshare.error.HttpErrorAnswerBuilder;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.storage.UserRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @LocalServerPort
  private int port;

  @Test
  public void postLogin200ITest() throws Exception {
    Credentials credentials = new Credentials();
    credentials.setEmail("email@email.com");
    credentials.setPassword("password");
    ResponseEntity<String> entity = this.testRestTemplate.postForEntity("/login", credentials, String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(userRepository.findOneByEmail("email@email.com").getId(), entity.getBody());
  }

  @Test
  public void postLogin400ITest() throws Exception {
    Credentials credentials = new Credentials();
    credentials.setEmail("email@email.com");
    ResponseEntity<String> entity = this.testRestTemplate.postForEntity("/login", credentials, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build400EmptyToString(), entity.getBody());
  }

  @Test
  public void postLogin401ITest() throws Exception {
    Credentials credentials = new Credentials();
    credentials.setEmail("email@email.com");
    credentials.setPassword("wrongPassword");
    ResponseEntity<String> entity = this.testRestTemplate.postForEntity("/login", credentials, String.class);
    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  public void getUsersInfo200ITest() throws Exception {
    int pageSize = 1;
    int pageNumber = 0;
    String searchString = "email@email.com";
    UserInfo expectedUserInfo = userRepository.findOneByEmail("email@email.com").toUserInfo();
    UserInfo[] expectedUserInfos = { expectedUserInfo };
    ResponseEntity<String> entity = this.testRestTemplate.withBasicAuth("admin", "admin").getForEntity(
        "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}", String.class,
        pageSize, pageNumber, searchString);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(TestHelper.asJsonString(expectedUserInfos), entity.getBody());
  }

  @Test
  public void getUsersInfo400ITest() throws Exception {
    int pageSize = 0;
    int pageNumber = 0;
    String searchString = "email@email.com";
    ResponseEntity<String> entity = this.testRestTemplate.withBasicAuth("admin", "admin").getForEntity(
        "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}", String.class,
        pageSize, pageNumber, searchString);
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
    ResponseEntity<String> entity = this.testRestTemplate.withBasicAuth("username", "password").getForEntity(
        "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}", String.class,
        pageSize, pageNumber, searchString);
    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build403NotAuthorizedToString(), entity.getBody());
  }

  @Test
  public void putUserInfo200ITest() throws Exception {
    UserInfo expectedUserInfo = userRepository.findOneByEmail("email@email.com").toUserInfo();
    expectedUserInfo.setIsAdmin(true);

    String url = "http://localhost:" + port + "/user/" + expectedUserInfo.getId() + "/userInfo";

    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth("admin", "admin");
    HttpEntity<UserInfo> httpEntity = new HttpEntity<>(expectedUserInfo, headers);

    ResponseEntity<String> entity = this.testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(TestHelper.asJsonString(expectedUserInfo), entity.getBody());
  }

  @Test
  public void getUserInfo200ITest() throws Exception {
    UserInfo expectedUserInfo = userRepository.findOneByEmail("email@email.com").toUserInfo();
    ResponseEntity<String> entity = this.testRestTemplate.withBasicAuth("admin", "admin")
        .getForEntity("/user/" + expectedUserInfo.getId() + "/userInfo", String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(TestHelper.asJsonString(expectedUserInfo), entity.getBody());
  }

  @Test
  public void openIdConnect200() throws Exception {
    String url = "http://localhost:" + port + "/openidConnectAuthorization";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(Base64.getEncoder().encodeToString(
        "cced798a-c8f7-4c79-aced-de9402655dc3.bf232a1e-fd09-44b6-8e71-b20b876a79c2.211002bc-e649-47a2-9b3d-8b3fc5385e3e"
            .getBytes()));
    HttpEntity<String> httpEntity = new HttpEntity<>("expectedUserInfo", headers);

    ResponseEntity<String> entity = this.testRestTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
  }

}