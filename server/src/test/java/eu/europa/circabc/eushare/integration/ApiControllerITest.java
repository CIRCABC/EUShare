/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.integration;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.circabc.eushare.error.HttpErrorAnswerBuilder;
import eu.europa.circabc.eushare.model.FileInfoRecipient;
import eu.europa.circabc.eushare.model.FileInfoUploader;
import eu.europa.circabc.eushare.model.FileInfoUploader.StatusEnum;
import eu.europa.circabc.eushare.model.FileRequest;
import eu.europa.circabc.eushare.model.Recipient;
import eu.europa.circabc.eushare.model.UserInfo;
import eu.europa.circabc.eushare.storage.entity.DBFile;
import eu.europa.circabc.eushare.storage.entity.DBShare;
import eu.europa.circabc.eushare.storage.entity.DBUser;
import eu.europa.circabc.eushare.storage.repository.FileRepository;
import eu.europa.circabc.eushare.storage.repository.ShareRepository;
import eu.europa.circabc.eushare.storage.repository.UserRepository;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
    private ShareRepository shareRepository;

    @MockBean
    private OpaqueTokenIntrospector opaqueTokenIntrospector;

    @LocalServerPort
    private int port;

    @Before
    public void createDefaultUsers() {
        DBUser admin = userRepository.findOneByUsername("admin");
        if (admin == null) {
            admin = DBUser.createUser(
                    "admin@admin.com",
                    "admin",
                    1024000000,
                    "admin", DBUser.Role.INTERNAL);
            admin.setRole(DBUser.Role.ADMIN);
            userRepository.save(admin);
        }

        DBUser username = userRepository.findOneByUsername("username");
        if (username == null) {
            DBUser defautUser = DBUser.createUser(
                    "email@email.com",
                    "name",
                    1024000000,
                    "username", DBUser.Role.INTERNAL);
            userRepository.save(defautUser);
        }
        assert (true);
    }

    @Test
    public void createDefaultUser() throws Exception {
        HttpEntity httpEntity = this.httpEntityAsInternalUser("");
        ResponseEntity<String> entity = this.testRestTemplate.postForEntity("/login", httpEntity, String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assert (entity
                .getBody()
                .contains(
                        userRepository.findOneByEmailIgnoreCase("email@email.com").getId()));
    }

    @Test
    public void postLogin200() throws Exception {
        HttpEntity httpEntity = this.httpEntityAsInternalUser("");
        ResponseEntity<String> entity = this.testRestTemplate.postForEntity("/login", httpEntity, String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assert (entity
                .getBody()
                .contains(
                        userRepository.findOneByEmailIgnoreCase("email@email.com").getId()));
    }

    @Test
    public void postLogin401() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
        httpHeaders.setBearerAuth("token");
        HttpEntity httpEntity = new HttpEntity<String>("", httpHeaders);

        ResponseEntity<String> entity = this.testRestTemplate.postForEntity("/login", httpEntity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build401EmptyToString(),
                entity.getBody());
    }

    @Test
    public void getUsersInfo200() throws Exception {
        HttpEntity<String> httpEntity = httpEntityAsAdmin("");
        int pageSize = 1;
        int pageNumber = 0;
        String sortBy = "name";
        String searchString = "email@email.com";
        Boolean active = true;

        // because user did not share any file we do not expect to find her via search
        // even if she exists
        UserInfo[] expectedUserInfos = {};

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}&active={active}&sortBy={sortBy}",
                HttpMethod.GET,
                httpEntity,
                String.class,
                pageSize,
                pageNumber,
                searchString,
                active,
                sortBy);

        assertEquals(HttpStatus.OK, entity.getStatusCode());

        assertEquals(
                ApiControllerITest.asJsonString(expectedUserInfos),
                entity.getBody());
    }

    @Test
    public void getUsersInfo400() throws Exception {
        HttpEntity httpEntity = httpEntityAsAdmin("");

        int pageSize = 0;
        int pageNumber = 0;
        String searchString = "email@email.com";
        String sortBy = "name";

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}&sortBy={sortBy}",
                HttpMethod.GET,
                httpEntity,
                String.class,
                pageSize,
                pageNumber,
                searchString,
                sortBy);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build400EmptyToString(),
                entity.getBody());
    }

    @Test
    public void getUsersInfo401() throws Exception {
        int pageSize = 1;
        int pageNumber = 0;
        String searchString = "email@email.com";
        ResponseEntity<String> entity = this.testRestTemplate.getForEntity(
                "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&searchString={searchString}",
                String.class,
                pageSize,
                pageNumber,
                searchString);
        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build401EmptyToString(),
                entity.getBody());
    }

    @Test
    public void getUsersInfo403() throws Exception {
        int pageSize = 1;
        int pageNumber = 0;
        String searchString = "email@email.com";
        String sortBy = "name";
        Boolean active = true;

        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(searchString);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/users/userInfo?pageSize={pageSize}&pageNumber={pageNumber}&sortBy={sortBy}&searchString={searchString}&active={active}",
                HttpMethod.GET,
                httpEntity,
                String.class,
                pageSize,
                pageNumber,
                sortBy,
                searchString,
                active);
        assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
                entity.getBody());
    }

    @Test
    public void putUserInfo200() throws Exception {
        UserInfo expectedUserInfo = userRepository
                .findOneByEmailIgnoreCase("admin@admin.com")
                .toUserInfo();
        expectedUserInfo.setIsAdmin(true);
        HttpEntity<String> httpEntity = this.httpEntityAsAdmin(ApiControllerITest.asJsonString(expectedUserInfo));
        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" + expectedUserInfo.getId() + "/userInfo",
                HttpMethod.PUT,
                httpEntity,
                String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(
                ApiControllerITest.asJsonString(expectedUserInfo),
                entity.getBody());
    }

    @Test
    public void putUserInfo401() throws Exception {
        UserInfo expectedUserInfo = userRepository
                .findOneByEmailIgnoreCase("email@email.com")
                .toUserInfo();
        expectedUserInfo.setIsAdmin(true);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
        HttpEntity<String> httpEntity = new HttpEntity<String>(
                ApiControllerITest.asJsonString(expectedUserInfo),
                httpHeaders);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" + expectedUserInfo.getId() + "/userInfo",
                HttpMethod.PUT,
                httpEntity,
                String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build401EmptyToString(),
                entity.getBody());
    }

    @Test
    public void putUserInfo403() throws Exception {
        UserInfo expectedUserInfo = userRepository
                .findOneByEmailIgnoreCase("email@email.com")
                .toUserInfo();
        expectedUserInfo.setIsAdmin(true);
        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(
                ApiControllerITest.asJsonString(expectedUserInfo));
        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" + expectedUserInfo.getId() + "/userInfo",
                HttpMethod.PUT,
                httpEntity,
                String.class);
        assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
                entity.getBody());
    }

    @Test
    public void putUserInfo404() throws Exception {
        UserInfo expectedUserInfo = userRepository
                .findOneByEmailIgnoreCase("email@email.com")
                .toUserInfo();
        expectedUserInfo.setIsAdmin(true);
        expectedUserInfo.setId("wrongId");

        HttpEntity<String> httpEntity = this.httpEntityAsAdmin(ApiControllerITest.asJsonString(expectedUserInfo));
        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" + expectedUserInfo.getId() + "/userInfo",
                HttpMethod.PUT,
                httpEntity,
                String.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build404EmptyToString(),
                entity.getBody());
    }

    @Test
    public void getUserInfo200() throws Exception {
        UserInfo expectedUserInfo = userRepository
                .findOneByEmailIgnoreCase("email@email.com")
                .toUserInfo();
        HttpEntity<String> httpEntity = this.httpEntityAsAdmin("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" + expectedUserInfo.getId() + "/userInfo",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(
                ApiControllerITest.asJsonString(expectedUserInfo),
                entity.getBody());
    }

    @Test
    public void getUserInfo401() throws Exception {
        UserInfo expectedUserInfo = userRepository
                .findOneByEmailIgnoreCase("email@email.com")
                .toUserInfo();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
        HttpEntity<String> httpEntity = new HttpEntity<String>(
                ApiControllerITest.asJsonString(expectedUserInfo),
                httpHeaders);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" + expectedUserInfo.getId() + "/userInfo",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build401EmptyToString(),
                entity.getBody());
    }

    @Test
    public void getUserInfo403() throws Exception {
        UserInfo expectedUserInfo = userRepository
                .findOneByEmailIgnoreCase("admin@admin.com")
                .toUserInfo();
        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" + expectedUserInfo.getId() + "/userInfo",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
                entity.getBody());
    }

    @Test
    public void getUserInfo404() throws Exception {
        HttpEntity<String> httpEntity = this.httpEntityAsAdmin("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" + "wrongId" + "/userInfo",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build404EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void getFilesFileInfoUploader200() throws Exception {
        // Creating the uploaded file
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "filename",
                1024,
                LocalDate.now(),
                "/a/sample/path");
        dbFile.setStatus(DBFile.Status.AVAILABLE);

        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        FileInfoUploader expecterFileInfoUploader = dbFile.toFileInfoUploader();
        FileInfoUploader[] expectedFileInfoUploaderArray = new FileInfoUploader[1];
        expectedFileInfoUploaderArray[0] = expecterFileInfoUploader;

        HttpEntity<String> httpEntity = this.httpEntityAsAdmin("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" +
                        uploaderSaved.getId() +
                        "/files/fileInfoUploader?pageSize=2&pageNumber=0",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(
                ApiControllerITest.asJsonString(expectedFileInfoUploaderArray),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void getFilesFileInfoUploader401() throws Exception {
        // Creating the uploaded file
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "filename",
                1024,
                LocalDate.now(),
                "/a/sample/path");
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        FileInfoUploader expecterFileInfoUploader = dbFile.toFileInfoUploader();
        FileInfoUploader[] expectedFileInfoUploaderArray = new FileInfoUploader[1];
        expectedFileInfoUploaderArray[0] = expecterFileInfoUploader;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" +
                        uploaderSaved.getId() +
                        "/files/fileInfoUploader?pageSize=2&pageNumber=0",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build401EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void getFilesFileInfoUploader403() throws Exception {
        // Creating the uploaded file
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "filename",
                1024,
                LocalDate.now(),
                "/a/sample/path");
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" +
                        uploaderSaved.getId() +
                        "/files/fileInfoUploader?pageSize=2&pageNumber=0",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void getFilesFileInfoUploader404() throws Exception {
        HttpEntity<String> httpEntity = this.httpEntityAsAdmin("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" +
                        "dummyId" +
                        "/files/fileInfoUploader?pageSize=2&pageNumber=0",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build404EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void getFilesFileInfoRecipient200() throws Exception {
        // Creating the uploaded file
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "filename",
                1024,
                LocalDate.now(),
                "/a/sample/path");
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());

        DBShare dbShare = new DBShare(uploader.getEmail(), dbFile, "message", true);
        dbShare.setShorturl("AAAAAA");
        shareRepository.save(dbShare);
        shareRepository.findOneByDownloadId(dbShare.getDownloadId());
        // Done

        FileInfoRecipient expecterFileInfoRecipient = dbFile.toFileInfoRecipient(
                uploader.getId());
        FileInfoRecipient[] expectedFileInfoRecipientArray = new FileInfoRecipient[1];
        expectedFileInfoRecipientArray[0] = expecterFileInfoRecipient;

        HttpEntity<String> httpEntity = this.httpEntityAsAdmin("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" +
                        uploaderSaved.getId() +
                        "/files/fileInfoRecipient?pageSize=2&pageNumber=0",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(
                ApiControllerITest.asJsonString(expectedFileInfoRecipientArray),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void getFilesFileInfoRecipient401() throws Exception {
        // Creating the uploaded file
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        uploader.setRole(DBUser.Role.ADMIN);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(DBUser.Role.ADMIN, uploaderSaved.getRole());

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "filename",
                1024,
                LocalDate.now(),
                "/a/sample/path");
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());

        DBShare dbShare = new DBShare(uploader.getEmail(), dbFile, "message", true);
        dbShare.setShorturl("AAAAAA");
        shareRepository.save(dbShare);
        shareRepository.findOneByDownloadId(dbShare.getDownloadId());
        // Done

        FileInfoRecipient expecterFileInfoRecipient = dbFile.toFileInfoRecipient(
                uploader.getId());
        FileInfoRecipient[] expectedFileInfoRecipientArray = new FileInfoRecipient[1];
        expectedFileInfoRecipientArray[0] = expecterFileInfoRecipient;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" +
                        uploaderSaved.getId() +
                        "/files/fileInfoRecipient?pageSize=2&pageNumber=0",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build401EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void getFilesFileInfoRecipient403() throws Exception {
        // Creating the uploaded file
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "filename",
                1024,
                LocalDate.now(),
                "/a/sample/path");
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());

        DBShare dbShare = new DBShare(uploader.getEmail(), dbFile, "message", true);
        dbShare.setShorturl("AAAAAA");
        shareRepository.save(dbShare);
        shareRepository.findOneByDownloadId(dbShare.getDownloadId());
        // Done

        FileInfoRecipient expecterFileInfoRecipient = dbFile.toFileInfoRecipient(
                uploader.getId());
        FileInfoRecipient[] expectedFileInfoRecipientArray = new FileInfoRecipient[1];
        expectedFileInfoRecipientArray[0] = expecterFileInfoRecipient;

        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" +
                        uploaderSaved.getId() +
                        "/files/fileInfoRecipient?pageSize=2&pageNumber=0",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void getFilesFileInfoRecipient404() throws Exception {
        HttpEntity<String> httpEntity = this.httpEntityAsAdmin("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/user/" +
                        "dummyId" +
                        "/files/fileInfoRecipient?pageSize=2&pageNumber=0",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build404EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void getFile200() throws Exception { // NOSONAR
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "file.txt",
                13,
                LocalDate.now(),
                path);
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

        ResponseEntity<Resource> entity = this.testRestTemplate.exchange(
                "/file/" + dbFile.getId(),
                HttpMethod.GET,
                httpEntity,
                Resource.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void getFile401() throws Exception { // NOSONAR
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "file.txt",
                13,
                LocalDate.now(),
                path,
                "dummyPassword");
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + dbFile.getId() + "?password=",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build401EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void getFile404() throws Exception { // NOSONAR
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + "dummyId" + "?password=",
                HttpMethod.GET,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build404FileNotFoundToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void deleteFile200() throws Exception { // NOSONAR
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "file.txt",
                13,
                LocalDate.now(),
                path);
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        HttpEntity httpEntity = this.httpEntityAsAdmin("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + dbFile.getId(),
                HttpMethod.DELETE,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void deleteFile401() throws Exception { // NOSONAR
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "file.txt",
                13,
                LocalDate.now(),
                path);
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + dbFile.getId(),
                HttpMethod.DELETE,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build401EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void deleteFile403() throws Exception { // NOSONAR
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "file.txt",
                13,
                LocalDate.now(),
                path);
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        HttpEntity httpEntity = this.httpEntityAsInternalUser("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + dbFile.getId(),
                HttpMethod.DELETE,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void deleteFile404() throws Exception { // NOSONAR
        HttpEntity httpEntity = this.httpEntityAsInternalUser("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + "dummyId",
                HttpMethod.DELETE,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build404EmptyToString(),
                entity.getBody());
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
        fileRequest.downloadNotification(false);
        Recipient recipient = new Recipient();
        recipient.setEmail("email@email.com");
        recipient.setMessage("message");

        fileRequest.setSharedWith(Arrays.asList(recipient));

        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(
                ApiControllerITest.asJsonString(fileRequest));

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/fileRequest",
                HttpMethod.POST,
                httpEntity,
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
        recipient.setEmail("email@email.com");
        recipient.setMessage("message");

        fileRequest.setSharedWith(Arrays.asList(recipient));

        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(
                ApiControllerITest.asJsonString(fileRequest));

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/fileRequest",
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build400EmptyToString(),
                entity.getBody());
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
        recipient.setEmail("email@email.com");
        recipient.setMessage("message");

        fileRequest.setSharedWith(Arrays.asList(recipient));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/fileRequest",
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build401EmptyToString(),
                entity.getBody());
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
        recipient.setEmail("email@email.com");
        recipient.setMessage("message");

        fileRequest.setSharedWith(Arrays.asList(recipient));

        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(
                ApiControllerITest.asJsonString(fileRequest));

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/fileRequest",
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build403IllegalFileSizeToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void postFileSharedWith200() {
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "file.txt",
                13,
                LocalDate.now(),
                path);
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        Recipient recipient = new Recipient();
        recipient.setEmail("EMAIL@EMAIL.COM");
        recipient.setMessage("message");
        recipient.setDownloadNotification(false);

        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(
                ApiControllerITest.asJsonString(recipient),
                "emailA@email.com",
                "uniqueUsername");

        ResponseEntity<Recipient> entity = this.testRestTemplate.exchange(
                "/file/" + dbFile.getId() + "/fileRequest/sharedWith",
                HttpMethod.POST,
                httpEntity,
                Recipient.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(
                recipient.getEmail().toLowerCase(),
                entity.getBody().getEmail());
        assertEquals(recipient.getMessage(), entity.getBody().getMessage());
    }

    @Test
    @DirtiesContext
    public void postFileSharedWith400() {
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "file.txt",
                13,
                LocalDate.now(),
                path);
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        Recipient recipient = new Recipient();
        recipient.setEmail(null);
        recipient.setMessage("message");

        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(
                ApiControllerITest.asJsonString(recipient),
                "emailA@email.com",
                "stupidUsername");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + dbFile.getId() + "/fileRequest/sharedWith",
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build400EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void postFileSharedWith401() {
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "file.txt",
                13,
                LocalDate.now(),
                path);
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        Recipient recipient = new Recipient();
        recipient.setEmail(null);
        recipient.setMessage("message");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", httpHeaders);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + dbFile.getId() + "/fileRequest/sharedWith",
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build401EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void postFileSharedWith403() {
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "file.txt",
                13,
                LocalDate.now(),
                path);
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        Recipient recipient = new Recipient();
        recipient.setEmail("email@email.com");
        recipient.setMessage("message");

        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(ApiControllerITest.asJsonString(recipient));

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + dbFile.getId() + "/fileRequest/sharedWith",
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void postFileSharedWith404() {
        Recipient recipient = new Recipient();
        recipient.setEmail("email@email.com");
        recipient.setMessage("message");

        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser(ApiControllerITest.asJsonString(recipient));

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + "dummyId" + "/fileRequest/sharedWith",
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build404EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void postFileFileContent200() throws Exception {
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
        Resource fileSystemResource = new ClassPathResource(
                "file.txt",
                this.getClass().getClassLoader());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);

        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "file.txt",
                13,
                LocalDate.now(),
                path);
        dbFile.setStatus(DBFile.Status.ALLOCATED);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        FileInfoUploader fileInfoUploader = dbFile.toFileInfoUploader();

        HttpEntity<MultiValueMap<String, Object>> httpEntity = this.httpEntityAsInternalUser(body, "emailA@email.com",
                "uniqueUsername");

        ResponseEntity<FileInfoUploader> entity = this.testRestTemplate.postForEntity(
                "/file/" + dbFile.getId() + "/fileRequest/fileContent",
                httpEntity,
                FileInfoUploader.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());

        // after post status is availible
        fileInfoUploader.setStatus(StatusEnum.AVAILABLE);
        assertEquals(fileInfoUploader, entity.getBody());
    }

    @Test
    @DirtiesContext
    public void postFileFileContent400() {
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/emptyfile.txt";
        Resource fileSystemResource = new ClassPathResource(
                "emptyfile.txt",
                this.getClass().getClassLoader());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);

        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "emptyfile.txt",
                13,
                LocalDate.now(),
                path);
        dbFile.setStatus(DBFile.Status.ALLOCATED);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        HttpEntity<MultiValueMap<String, Object>> httpEntity = this.httpEntityAsInternalUser(body, "emailA@email.com",
                "stupidUsername");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + dbFile.getId() + "/fileRequest/fileContent",
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build400EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void postFileFileContent401() {
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
        Resource fileSystemResource = new ClassPathResource(
                "file.txt",
                this.getClass().getClassLoader());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);

        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "file.txt",
                13,
                LocalDate.now(),
                path);
        dbFile.setStatus(DBFile.Status.ALLOCATED);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        HttpEntity<MultiValueMap<String, Object>> httpEntity = this.httpEntityAsAnonymousUser(body);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + dbFile.getId() + "/fileRequest/fileContent",
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build401EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void postFileFileContent403() {
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        String path = resourcesDirectory.getAbsolutePath() + "/file.txt";
        Resource fileSystemResource = new ClassPathResource(
                "file.txt",
                this.getClass().getClassLoader());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);

        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "file.txt",
                13,
                LocalDate.now(),
                path);
        dbFile.setStatus(DBFile.Status.ALLOCATED);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());
        // Done

        HttpEntity<MultiValueMap<String, Object>> httpEntity = this.httpEntityAsInternalUser(body);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + dbFile.getId() + "/fileRequest/fileContent",
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void postFileFileContent404() {
        // Creating the uploaded file
        File resourcesDirectory = new File("src/test/resources");
        Resource fileSystemResource = new ClassPathResource(
                "file.txt",
                this.getClass().getClassLoader());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = this.httpEntityAsInternalUser(body);

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" + "dummyId" + "/fileRequest/fileContent",
                HttpMethod.POST,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build404EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void deleteFileSharedWithUser200() throws Exception {
        // Creating the uploaded file
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "filename",
                1024,
                LocalDate.now(),
                "/a/sample/path");
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());

        DBShare dbShare = new DBShare(uploader.getEmail(), dbFile, "message", true);
        dbShare.setShorturl("AAAAAA");
        shareRepository.save(dbShare);
        shareRepository.findOneByDownloadId(dbShare.getDownloadId());
        // Done

        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("", "emailA@email.com", "uniqueUsername");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" +
                        dbFile.getId() +
                        "/fileRequest/sharedWith?userID=" +
                        uploader.getId(),
                HttpMethod.DELETE,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void deleteFileSharedWithUser401() throws Exception {
        // Creating the uploaded file
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "filename",
                1024,
                LocalDate.now(),
                "/a/sample/path");
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());

        DBShare dbShare = new DBShare(uploader.getEmail(), dbFile, "message", true);
        dbShare.setShorturl("AAAAAA");
        shareRepository.save(dbShare);
        shareRepository.findOneByDownloadId(dbShare.getDownloadId());
        // Done

        HttpEntity<String> httpEntity = this.httpEntityAsAnonymousUser("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" +
                        dbFile.getId() +
                        "/fileRequest/sharedWith?userID=" +
                        uploader.getId(),
                HttpMethod.DELETE,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build401EmptyToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void deleteFileSharedWithUser403() throws Exception {
        // Creating the uploaded file
        DBUser uploader = DBUser.createUser(
                "emailA@email.com",
                "uniqueName",
                1024,
                "uniqueUsername", DBUser.Role.INTERNAL);
        userRepository.save(uploader);
        DBUser uploaderSaved = userRepository.findOneByEmailIgnoreCase(
                "emailA@email.com");
        assertEquals(uploader, uploaderSaved);

        DBFile dbFile = new DBFile(
                uploaderSaved,
                Collections.emptySet(),
                "filename",
                1024,
                LocalDate.now(),
                "/a/sample/path");
        dbFile.setStatus(DBFile.Status.AVAILABLE);
        fileRepository.save(dbFile);
        fileRepository.findOneById(dbFile.getId());

        DBShare dbShare = new DBShare(uploader.getEmail(), dbFile, "message", true);
        dbShare.setShorturl("AAAAAA");
        shareRepository.save(dbShare);
        shareRepository.findOneByDownloadId(dbShare.getDownloadId());
        // Done

        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/" +
                        dbFile.getId() +
                        "/fileRequest/sharedWith?userID=" +
                        uploader.getId(),
                HttpMethod.DELETE,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.FORBIDDEN, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build403NotAuthorizedToString(),
                entity.getBody());
    }

    @Test
    @DirtiesContext
    public void deleteFileSharedWithUser404() throws Exception {
        HttpEntity<String> httpEntity = this.httpEntityAsInternalUser("");

        ResponseEntity<String> entity = this.testRestTemplate.exchange(
                "/file/dummyId/fileRequest/sharedWith?userID=dummyId",
                HttpMethod.DELETE,
                httpEntity,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertEquals(
                HttpErrorAnswerBuilder.build404FileNotFoundToString(),
                entity.getBody());
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

    public <T> HttpEntity<T> httpEntityAsAdmin(T body) {
        return this.httpEntityAsInternalUser(body, "admin@admin.com", "admin");
    }

    public <T> HttpEntity<T> httpEntityAsInternalUser(T body) {
        return this.httpEntityAsInternalUser(body, "email@email.com", "username");
    }

    public <T> HttpEntity<T> httpEntityAsInternalUser(
            T body,
            String email,
            String username) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (body instanceof Resource) {
            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        if (body instanceof String) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        }
        if (body instanceof MultiValueMap) {
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        }
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
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
        if (body instanceof MultiValueMap) {
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        }
        httpHeaders.setOrigin("http://localhost:8080");
        httpHeaders.set("Referer", "http://localhost:8080");
        HttpEntity httpEntity = new HttpEntity<T>(body, httpHeaders);
        return httpEntity;
    }

    public void authenticateAs(String email, String username) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("username", username);
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
                "INTERNAL");
        Collection<GrantedAuthority> collection = new LinkedList();

        collection.add(grantedAuthority);
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal = new DefaultOAuth2AuthenticatedPrincipal(
                username,
                attributes,
                collection);
        when(opaqueTokenIntrospector.introspect(anyString()))
                .thenReturn(oAuth2AuthenticatedPrincipal);
    }
}
