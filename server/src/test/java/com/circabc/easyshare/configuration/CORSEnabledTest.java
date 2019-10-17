/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.configuration;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import com.circabc.easyshare.TestHelper;
import com.circabc.easyshare.model.Credentials;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CORSEnabledTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void corsWithAnnotationTest() throws Exception {
        Credentials credentials = new Credentials();
        credentials.setEmail("admin");
        credentials.setPassword("admin");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setOrigin("http://localhost:8080");
        HttpEntity httpEntity = new HttpEntity<String>(TestHelper.asJsonString(credentials), httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(uri("/login"), httpEntity, String.class);
        assertEquals("http://localhost:8080", response.getHeaders().getAccessControlAllowOrigin());
        String responseBody = response.getBody();
        assertEquals(String.class, responseBody.getClass());
    }

    private URI uri(String path) {
        return restTemplate.getRestTemplate().getUriTemplateHandler().expand(path);
    }
}