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

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.circabc.easyshare.error.HttpErrorAnswerBuilder;
import com.circabc.easyshare.model.FileInfoRecipient;
import com.circabc.easyshare.model.FileInfoUploader;
import com.circabc.easyshare.model.FileRequest;
import com.circabc.easyshare.model.Recipient;
import com.circabc.easyshare.model.RecipientWithLink;
import com.circabc.easyshare.model.UserInfo;
import com.circabc.easyshare.storage.DBFile;
import com.circabc.easyshare.storage.DBUser;
import com.circabc.easyshare.storage.DBUserFile;
import com.circabc.easyshare.storage.FileRepository;
import com.circabc.easyshare.storage.UserFileRepository;
import com.circabc.easyshare.storage.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApiControllerITest {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FileRepository fileRepository;

  @Autowired
  private UserFileRepository userFileRepository;

  @MockBean
  private OpaqueTokenIntrospector opaqueTokenIntrospector;

  @LocalServerPort
  private int port;

  @Test
  public void postLogin200() throws Exception {
    HttpEntity httpEntity = this.httpEntityAsInternalUser("");
    ResponseEntity<String> entity = this.testRestTemplate.postForEntity("/login", httpEntity, String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(userRepository.findOneByEmailIgnoreCase("email@email.com").getId(), entity.getBody());
  }

  @Test
  public void postLogin401() throws Exception {
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
  public void getUsersInfo200() throws Exception {
    HttpEntity httpEntity = httpEntityAsAdmin("");
    int pageSize = 1;
    int pageNumber = 0;
    String searchString = "email@email.com";
    UserInfo expectedUserInfo = userRepository.findOneByEmailIgnoreCase("email@email.com").toUserInfo();
    UserInfo[] expectedUserInfos = { expectedUserInfo };

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}", HttpMethod.GET,
        httpEntity, String.class, pageSize, pageNumber, searchString);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(ApiControllerITest.asJsonString(expectedUserInfos), entity.getBody());
  }

  @Test
  public void getUsersInfo400() throws Exception {
    HttpEntity httpEntity = httpEntityAsAdmin("");

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
  public void getUsersInfo401() throws Exception {
    int pageSize = 1;
    int pageNumber = 0;
    String searchString = "email@email.com";
    ResponseEntity<String> entity = this.testRestTemplate.getForEntity(
        "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}", String.class,
        pageSize, pageNumber, searchString);
    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  public void getUsersInfo403() throws Exception {
    int pageSize = 1;
    int pageNumber = 0;
    String searchString = "email@email.com";

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(searchString);

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}", HttpMethod.GET,
        httpEntity, String.class, pageSize, pageNumber, searchString);
    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build403NotAuthorizedToString(), entity.getBody());
  }

  @Test
  public void putUserInfo200() throws Exception {
    UserInfo expectedUserInfo = userRepository.findOneByEmailIgnoreCase("admin@admin.com").toUserInfo();
    expectedUserInfo.setIsAdmin(true);
    HttpEntity<String> httpEntity = this.httpEntityAsAdmin(ApiControllerITest.asJsonString(expectedUserInfo));
    ResponseEntity<String> entity = this.testRestTemplate.exchange("/user/" + expectedUserInfo.getId() + "/userInfo",
        HttpMethod.PUT, httpEntity, String.class);
    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(ApiControllerITest.asJsonString(expectedUserInfo), entity.getBody());
  }

  @Test
  public void putUserInfo401() throws Exception {
    UserInfo expectedUserInfo = userRepository.findOneByEmailIgnoreCase("email@email.com").toUserInfo();
    expectedUserInfo.setIsAdmin(true);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    HttpEntity<String> httpEntity = new HttpEntity<String>(ApiControllerITest.asJsonString(expectedUserInfo),
        httpHeaders);

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/user/" + expectedUserInfo.getId() + "/userInfo",
        HttpMethod.PUT, httpEntity, String.class);
    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  public void putUserInfo403() throws Exception {
    UserInfo expectedUserInfo = userRepository.findOneByEmailIgnoreCase("email@email.com").toUserInfo();
    expectedUserInfo.setIsAdmin(true);
    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(ApiControllerITest.asJsonString(expectedUserInfo));
    ResponseEntity<String> entity = this.testRestTemplate.exchange("/user/" + expectedUserInfo.getId() + "/userInfo",
        HttpMethod.PUT, httpEntity, String.class);
    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build403NotAuthorizedToString(), entity.getBody());
  }

  @Test
  public void putUserInfo404() throws Exception {
    UserInfo expectedUserInfo = userRepository.findOneByEmailIgnoreCase("email@email.com").toUserInfo();
    expectedUserInfo.setIsAdmin(true);
    expectedUserInfo.setId("wrongId");

    HttpEntity<String> httpEntity = this.httpEntityAsAdmin(ApiControllerITest.asJsonString(expectedUserInfo));
    ResponseEntity<String> entity = this.testRestTemplate.exchange("/user/" + expectedUserInfo.getId() + "/userInfo",
        HttpMethod.PUT, httpEntity, String.class);
    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build404EmptyToString(), entity.getBody());
  }

  @Test
  public void getUserInfo200() throws Exception {
    UserInfo expectedUserInfo = userRepository.findOneByEmailIgnoreCase("email@email.com").toUserInfo();
    HttpEntity<String> httpEntity = this.httpEntityAsAdmin("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/user/" + expectedUserInfo.getId() + "/userInfo",
        HttpMethod.GET, httpEntity, String.class);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(ApiControllerITest.asJsonString(expectedUserInfo), entity.getBody());
  }

  @Test
  public void getUserInfo401() throws Exception {
    UserInfo expectedUserInfo = userRepository.findOneByEmailIgnoreCase("email@email.com").toUserInfo();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    HttpEntity<String> httpEntity = new HttpEntity<String>(ApiControllerITest.asJsonString(expectedUserInfo),
        httpHeaders);

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/user/" + expectedUserInfo.getId() + "/userInfo",
        HttpMethod.GET, httpEntity, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  public void getUserInfo403() throws Exception {
    UserInfo expectedUserInfo = userRepository.findOneByEmailIgnoreCase("admin@admin.com").toUserInfo();
    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/user/" + expectedUserInfo.getId() + "/userInfo",
        HttpMethod.GET, httpEntity, String.class);

    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build403NotAuthorizedToString(), entity.getBody());
  }

  @Test
  public void getUserInfo404() throws Exception {
    HttpEntity<String> httpEntity = this.httpEntityAsAdmin("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/user/" + "wrongId" + "/userInfo", HttpMethod.GET,
        httpEntity, String.class);

    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build404EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void getFilesFileInfoUploader200() throws Exception {
    // Creating the uploaded file
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "filename", 1024,
        LocalDate.now(), "/a/sample/path");
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    FileInfoUploader expecterFileInfoUploader = dbFile.toFileInfoUploader();
    FileInfoUploader[] expectedFileInfoUploaderArray = new FileInfoUploader[1];
    expectedFileInfoUploaderArray[0] = expecterFileInfoUploader;

    HttpEntity<String> httpEntity = this.httpEntityAsAdmin("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/user/" + uploaderSaved.getId() + "/files/fileInfoUploader?pageSize=2&pageNumber=0", HttpMethod.GET,
        httpEntity, String.class);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(ApiControllerITest.asJsonString(expectedFileInfoUploaderArray), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void getFilesFileInfoUploader401() throws Exception {
    // Creating the uploaded file
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "filename", 1024,
        LocalDate.now(), "/a/sample/path");
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    FileInfoUploader expecterFileInfoUploader = dbFile.toFileInfoUploader();
    FileInfoUploader[] expectedFileInfoUploaderArray = new FileInfoUploader[1];
    expectedFileInfoUploaderArray[0] = expecterFileInfoUploader;

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/user/" + uploaderSaved.getId() + "/files/fileInfoUploader?pageSize=2&pageNumber=0", HttpMethod.GET,
        httpEntity, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void getFilesFileInfoUploader403() throws Exception {
    // Creating the uploaded file
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "filename", 1024,
        LocalDate.now(), "/a/sample/path");
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/user/" + uploaderSaved.getId() + "/files/fileInfoUploader?pageSize=2&pageNumber=0", HttpMethod.GET,
        httpEntity, String.class);

    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build403NotAuthorizedToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void getFilesFileInfoUploader404() throws Exception {
    HttpEntity<String> httpEntity = this.httpEntityAsAdmin("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/user/" + "dummyId" + "/files/fileInfoUploader?pageSize=2&pageNumber=0", HttpMethod.GET, httpEntity,
        String.class);

    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build404EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void getFilesFileInfoRecipient200() throws Exception {
    // Creating the uploaded file
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "filename", 1024,
        LocalDate.now(), "/a/sample/path");
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");

    DBUserFile dbUserFile = new DBUserFile("downloadIdq7xobngy_1", uploader, dbFile);
    userFileRepository.save(dbUserFile);
    userFileRepository.findOneByDownloadId("downloadIdq7xobngy_1");
    // Done

    FileInfoRecipient expecterFileInfoRecipient = dbFile.toFileInfoRecipient(uploader.getId());
    FileInfoRecipient[] expectedFileInfoRecipientArray = new FileInfoRecipient[1];
    expectedFileInfoRecipientArray[0] = expecterFileInfoRecipient;

    HttpEntity<String> httpEntity = this.httpEntityAsAdmin("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/user/" + uploaderSaved.getId() + "/files/fileInfoRecipient?pageSize=2&pageNumber=0", HttpMethod.GET,
        httpEntity, String.class);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(ApiControllerITest.asJsonString(expectedFileInfoRecipientArray), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void getFilesFileInfoRecipient401() throws Exception {
    // Creating the uploaded file
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "filename", 1024,
        LocalDate.now(), "/a/sample/path");
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");

    DBUserFile dbUserFile = new DBUserFile("downloadIdq7xobngy_1", uploader, dbFile);
    userFileRepository.save(dbUserFile);
    userFileRepository.findOneByDownloadId("downloadIdq7xobngy_1");
    // Done

    FileInfoRecipient expecterFileInfoRecipient = dbFile.toFileInfoRecipient(uploader.getId());
    FileInfoRecipient[] expectedFileInfoRecipientArray = new FileInfoRecipient[1];
    expectedFileInfoRecipientArray[0] = expecterFileInfoRecipient;

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/user/" + uploaderSaved.getId() + "/files/fileInfoRecipient?pageSize=2&pageNumber=0", HttpMethod.GET,
        httpEntity, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void getFilesFileInfoRecipient403() throws Exception {
    // Creating the uploaded file
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "filename", 1024,
        LocalDate.now(), "/a/sample/path");
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");

    DBUserFile dbUserFile = new DBUserFile("downloadIdq7xobngy_1", uploader, dbFile);
    userFileRepository.save(dbUserFile);
    userFileRepository.findOneByDownloadId("downloadIdq7xobngy_1");
    // Done

    FileInfoRecipient expecterFileInfoRecipient = dbFile.toFileInfoRecipient(uploader.getId());
    FileInfoRecipient[] expectedFileInfoRecipientArray = new FileInfoRecipient[1];
    expectedFileInfoRecipientArray[0] = expecterFileInfoRecipient;

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/user/" + uploaderSaved.getId() + "/files/fileInfoRecipient?pageSize=2&pageNumber=0", HttpMethod.GET,
        httpEntity, String.class);

    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build403NotAuthorizedToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void getFilesFileInfoRecipient404() throws Exception {
    HttpEntity<String> httpEntity = this.httpEntityAsAdmin("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/user/" + "dummyId" + "/files/fileInfoRecipient?pageSize=2&pageNumber=0", HttpMethod.GET, httpEntity,
        String.class);

    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build404EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void getFile200() throws Exception { // NOSONAR
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path);
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

    ResponseEntity<Resource> entity = this.testRestTemplate.exchange("/file/" + dbFile.getId(), HttpMethod.GET,
        httpEntity, Resource.class);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
  }

  @Test
  @DirtiesContext
  public void getFile401() throws Exception { // NOSONAR
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path, "dummyPassword");
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/file/" + dbFile.getId() + "?password=",
        HttpMethod.GET, httpEntity, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void getFile404() throws Exception { // NOSONAR
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/file/" + "dummyId" + "?password=", HttpMethod.GET,
        httpEntity, String.class);

    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build404EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void deleteFile200() throws Exception { // NOSONAR
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path);
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    HttpEntity httpEntity = this.httpEntityAsAdmin("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/file/" + dbFile.getId(), HttpMethod.DELETE,
        httpEntity, String.class);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
  }

  @Test
  @DirtiesContext
  public void deleteFile401() throws Exception { // NOSONAR
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path);
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/file/" + dbFile.getId(), HttpMethod.DELETE,
        httpEntity, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void deleteFile403() throws Exception { // NOSONAR
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path);
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    HttpEntity httpEntity = this.httpEntityAsInternalUser("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/file/" + dbFile.getId(), HttpMethod.DELETE,
        httpEntity, String.class);

    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build403NotAuthorizedToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void deleteFile404() throws Exception { // NOSONAR
    HttpEntity httpEntity = this.httpEntityAsInternalUser("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/file/" + "dummyId", HttpMethod.DELETE, httpEntity,
        String.class);

    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build404EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void postFileFileRequest200() throws Exception { // NOSONAR
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
    fileRequest.setSharedWith(Arrays.asList(recipient));

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(ApiControllerITest.asJsonString(fileRequest));

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/file/fileRequest", HttpMethod.POST, httpEntity,
        String.class);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
  }

  @Test
  @DirtiesContext
  public void postFileFileRequest400() throws Exception { // NOSONAR
    FileRequest fileRequest = new FileRequest();
    fileRequest.setExpirationDate(LocalDate.now());
    fileRequest.setHasPassword(true);
    fileRequest.setName("name");
    fileRequest.setSize(new BigDecimal(1024));
    Recipient recipient = new Recipient();
    recipient.setEmailOrName("email@email.com");
    recipient.setMessage("message");
    recipient.setSendEmail(true);
    fileRequest.setSharedWith(Arrays.asList(recipient));

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(ApiControllerITest.asJsonString(fileRequest));

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/file/fileRequest", HttpMethod.POST, httpEntity,
        String.class);

    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build400EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void postFileFileRequest401() throws Exception { // NOSONAR
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
    fileRequest.setSharedWith(Arrays.asList(recipient));

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/file/fileRequest", HttpMethod.POST, httpEntity,
        String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void postFileFileRequest403() throws Exception { // NOSONAR
    FileRequest fileRequest = new FileRequest();
    fileRequest.setExpirationDate(LocalDate.now());
    fileRequest.setHasPassword(true);
    fileRequest.setPassword("password"); // NOSONAR
    fileRequest.setName("name");
    fileRequest.setSize(new BigDecimal(0));
    Recipient recipient = new Recipient();
    recipient.setEmailOrName("email@email.com");
    recipient.setMessage("message");
    recipient.setSendEmail(true);
    fileRequest.setSharedWith(Arrays.asList(recipient));

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(ApiControllerITest.asJsonString(fileRequest));

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/file/fileRequest", HttpMethod.POST, httpEntity,
        String.class);

    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build403IllegalFileSizeToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void postFileSharedWith200() {
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path);
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    Recipient recipient = new Recipient();
    recipient.setEmailOrName("EMAIL@EMAIL.COM");
    recipient.setMessage("message");
    recipient.setSendEmail(true);

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(ApiControllerITest.asJsonString(recipient),
        "emailA@email.com", "stupidUsername");

    ResponseEntity<RecipientWithLink> entity = this.testRestTemplate.exchange(
        "/file/" + dbFile.getId() + "/fileRequest/sharedWith", HttpMethod.POST, httpEntity, RecipientWithLink.class);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(recipient.getEmailOrName(), entity.getBody().getEmailOrName());
    assertEquals(recipient.getSendEmail(), entity.getBody().getSendEmail());
    assertEquals(recipient.getMessage(), entity.getBody().getMessage());
  }

  @Test
  @DirtiesContext
  public void postFileSharedWith400() {
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path);
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    Recipient recipient = new Recipient();
    recipient.setEmailOrName(null);
    recipient.setMessage("message");
    recipient.setSendEmail(true);

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(ApiControllerITest.asJsonString(recipient),
        "emailA@email.com", "stupidUsername");

    ResponseEntity<String> entity = this.testRestTemplate
        .exchange("/file/" + dbFile.getId() + "/fileRequest/sharedWith", HttpMethod.POST, httpEntity, String.class);

    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build400EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void postFileSharedWith401() {
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path);
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    Recipient recipient = new Recipient();
    recipient.setEmailOrName(null);
    recipient.setMessage("message");
    recipient.setSendEmail(true);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setOrigin("http://localhost:8080");
    HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

    ResponseEntity<String> entity = this.testRestTemplate
        .exchange("/file/" + dbFile.getId() + "/fileRequest/sharedWith", HttpMethod.POST, httpEntity, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void postFileSharedWith403() {
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path);
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    Recipient recipient = new Recipient();
    recipient.setEmailOrName("email@email.com");
    recipient.setMessage("message");
    recipient.setSendEmail(true);

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(ApiControllerITest.asJsonString(recipient));

    ResponseEntity<String> entity = this.testRestTemplate
        .exchange("/file/" + dbFile.getId() + "/fileRequest/sharedWith", HttpMethod.POST, httpEntity, String.class);

    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build403NotAuthorizedToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void postFileSharedWith404() {
    Recipient recipient = new Recipient();
    recipient.setEmailOrName("email@email.com");
    recipient.setMessage("message");
    recipient.setSendEmail(true);

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(ApiControllerITest.asJsonString(recipient));

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/file/" + "dummyId" + "/fileRequest/sharedWith",
        HttpMethod.POST, httpEntity, String.class);

    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build404EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void postFileFileContent200() {
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";

    Resource resource = new ClassPathResource("file.txt", this.getClass().getClassLoader());
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path);
    dbFile.setStatus(DBFile.Status.ALLOCATED);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    FileInfoUploader fileInfoUploader = dbFile.toFileInfoUploader();

    HttpEntity<Resource> httpEntity = this.httpEntityAsInternalUser(resource, "emailA@email.com", "stupidUsername");

    ResponseEntity<FileInfoUploader> entity = this.testRestTemplate.exchange(
        "/file/" + dbFile.getId() + "/fileRequest/fileContent", HttpMethod.POST, httpEntity, FileInfoUploader.class);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
    assertEquals(fileInfoUploader, entity.getBody());
  }

  @Test
  @DirtiesContext
  public void postFileFileContent400() {
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";

    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path);
    dbFile.setStatus(DBFile.Status.ALLOCATED);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("resource", "emailA@email.com", "stupidUsername");

    ResponseEntity<String> entity = this.testRestTemplate
        .exchange("/file/" + dbFile.getId() + "/fileRequest/fileContent", HttpMethod.POST, httpEntity, String.class);

    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build400EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void postFileFileContent401() {
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";

    Resource resource = new ClassPathResource("file.txt", this.getClass().getClassLoader());
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path);
    dbFile.setStatus(DBFile.Status.ALLOCATED);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    HttpEntity<Resource> httpEntity = this.httpEntityAsAnonymousUser(resource);

    ResponseEntity<String> entity = this.testRestTemplate
        .exchange("/file/" + dbFile.getId() + "/fileRequest/fileContent", HttpMethod.POST, httpEntity, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void postFileFileContent403() {
    // Creating the uploaded file
    File resourcesDirectory = new File("src/test/resources");
    String path = resourcesDirectory.getAbsolutePath() + "/file.txt";

    Resource resource = new ClassPathResource("file.txt", this.getClass().getClassLoader());
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "file.txt", 13,
        LocalDate.now(), path);
    dbFile.setStatus(DBFile.Status.ALLOCATED);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");
    // Done

    HttpEntity<Resource> httpEntity = this.httpEntityAsInternalUser(resource);

    ResponseEntity<String> entity = this.testRestTemplate
        .exchange("/file/" + dbFile.getId() + "/fileRequest/fileContent", HttpMethod.POST, httpEntity, String.class);

    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build403NotAuthorizedToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void postFileFileContent404() {
    Resource resource = new ClassPathResource("file.txt", this.getClass().getClassLoader());
    HttpEntity<Resource> httpEntity = this.httpEntityAsInternalUser(resource);

    ResponseEntity<String> entity = this.testRestTemplate.exchange("/file/" + "dummyId" + "/fileRequest/fileContent",
        HttpMethod.POST, httpEntity, String.class);

    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build404EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void deleteFileSharedWithUser200() throws Exception {
    // Creating the uploaded file
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "filename", 1024,
        LocalDate.now(), "/a/sample/path");
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");

    DBUserFile dbUserFile = new DBUserFile("downloadIdq7xobngy_1", uploader, dbFile);
    userFileRepository.save(dbUserFile);
    userFileRepository.findOneByDownloadId("downloadIdq7xobngy_1");
    // Done

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("", "emailA@email.com", "dummyUsername");

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/file/" + dbFile.getId() + "/fileRequest/sharedWith/" + uploader.getId(), HttpMethod.DELETE, httpEntity,
        String.class);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
  }

  @Test
  @DirtiesContext
  public void deleteFileSharedWithUser401() throws Exception {
    // Creating the uploaded file
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "filename", 1024,
        LocalDate.now(), "/a/sample/path");
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");

    DBUserFile dbUserFile = new DBUserFile("downloadIdq7xobngy_1", uploader, dbFile);
    userFileRepository.save(dbUserFile);
    userFileRepository.findOneByDownloadId("downloadIdq7xobngy_1");
    // Done

    HttpEntity<String> httpEntity = this.httpEntityAsAnonymousUser("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/file/" + dbFile.getId() + "/fileRequest/sharedWith/" + uploader.getId(), HttpMethod.DELETE, httpEntity,
        String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build401EmptyToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void deleteFileSharedWithUser403() throws Exception {
    // Creating the uploaded file
    DBUser uploader = DBUser.createInternalUser("emailA@email.com", "uniqueName", "password", 1024, "uniqueUsername");
    userRepository.save(uploader);
    DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase("emailA@email.com");
    assertEquals(uploader, uploaderSaved);

    DBFile dbFile = new DBFile("szgakjq2yso7xobngy_9", uploaderSaved, Collections.emptySet(), "filename", 1024,
        LocalDate.now(), "/a/sample/path");
    dbFile.setStatus(DBFile.Status.AVAILABLE);
    fileRepository.save(dbFile);
    fileRepository.findOneById("szgakjq2yso7xobngy_9");

    DBUserFile dbUserFile = new DBUserFile("downloadIdq7xobngy_1", uploader, dbFile);
    userFileRepository.save(dbUserFile);
    userFileRepository.findOneByDownloadId("downloadIdq7xobngy_1");
    // Done

    HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/file/" + dbFile.getId() + "/fileRequest/sharedWith/" + uploader.getId(), HttpMethod.DELETE, httpEntity,
        String.class);

    assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build403NotAuthorizedToString(), entity.getBody());
  }

  @Test
  @DirtiesContext
  public void deleteFileSharedWithUser404() throws Exception {
  HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("");

    ResponseEntity<String> entity = this.testRestTemplate.exchange(
        "/file/dummyId/fileRequest/sharedWith/dummyId", HttpMethod.DELETE, httpEntity,
        String.class);

    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    assertEquals(HttpErrorAnswerBuilder.build404FileNotFoundToString(), entity.getBody());
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

  public <T> HttpEntity<T> httpEntityAsAdmin(T body) {
    return this.httpEntityAsInternalUser(body, "admin@admin.com", "admin");
  }

  public <T> HttpEntity<T> httpEntityAsInternalUser(T body) {
    return this.httpEntityAsInternalUser(body, "email@email.com", "username");
  }

  public <T> HttpEntity<T> httpEntityAsInternalUser(T body, String email, String username) {
    HttpHeaders httpHeaders = new HttpHeaders();
    if (body instanceof Resource) {
      httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    }
    if (body instanceof String) {
      httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }
    httpHeaders.setOrigin("http://localhost:8080");
    httpHeaders.setBearerAuth("token");
    HttpEntity httpEntity = new HttpEntity<T>(body, httpHeaders);
    this.authenticateAs(email, username);
    return httpEntity;
  }

  public <T> HttpEntity<T> httpEntityAsAnonymousUser(T body) {
    HttpHeaders httpHeaders = new HttpHeaders();
    if (body instanceof Resource) {
      httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    }
    if (body instanceof String) {
      httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }
    httpHeaders.setOrigin("http://localhost:8080");
    HttpEntity httpEntity = new HttpEntity<T>(body, httpHeaders);
    return httpEntity;
  }

  public void authenticateAs(String email, String username) {
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", email);
    attributes.put("username", username);
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("INTERNAL");
    Collection<GrantedAuthority> collection = new LinkedList();

    collection.add(grantedAuthority);
    OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(username,
        attributes, collection);
    when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2AuthenticatedPrincipal);
  }

}