/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Service
public class CaptchaApiImpl implements CaptchaApi {

    private static final Log logger = LogFactory.getLog(CaptchaApiImpl.class);
    private String captchaUrl;

    private RestTemplate restTemplate = new RestTemplate();  // Spring's RestTemplate to replace HttpClient

    @Override
    public boolean validate(String captchaToken, String captchaId, String answer) {
        boolean result = false;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-jwtString", captchaToken);

        String url = this.captchaUrl;
        try {
            url = url + "/api/validateCaptcha/" +
                    URLEncoder.encode(captchaId, StandardCharsets.UTF_8.toString()) +
                    "?captchaAnswer=" +
                    URLEncoder.encode(answer, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Can not create url ", e);
            }
            return result;
        }

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(URI.create(url), headers, String.class);

        if (responseEntity.getStatusCodeValue() != 200) {
            if (logger.isErrorEnabled()) {
                logger.error("The Post method is not implemented by this URI: " + url);
            }
        } else {
            String response = responseEntity.getBody();
            if (logger.isInfoEnabled()) {
                logger.info("Request:\n" + url);
                logger.info("Response:\n" + response);
            }
            if (!response.contains("success")) {
                if (logger.isErrorEnabled()) {
                    logger.error("Call EU Captcha service :  " + url);
                    logger.error("Response is not successful :" + response);
                }
            } else {
                result = true;
            }
        }
        return result;
    }

    public String getCaptchaUrl() {
        return captchaUrl;
    }

    public void setCaptchaUrl(String captchaUrl) {
        this.captchaUrl = captchaUrl;
    }
}
