/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import eu.europa.circabc.eushare.CaptchaApiImpl;
import eu.europa.circabc.eushare.configuration.EushareConfiguration;
import eu.europa.circabc.eushare.exceptions.NoStackTraceResponseStatusException;


@Component
public class CaptchaValidator {

    @Autowired
    private EushareConfiguration esConfig;

    public void checkCaptcha(String captchaId, String captchaToken, String captchaText) {

        if (captchaToken == null || captchaId == null || captchaText == null) {
            throw new NoStackTraceResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "CAPTCHA parameters missing", null);
        }

        CaptchaApiImpl captchaApi = new CaptchaApiImpl();
        captchaApi.setCaptchaUrl(esConfig.getCaptchaUrl());

        boolean isValid = captchaApi.validate(captchaToken, captchaId, captchaText);

        if (!isValid) {
            throw new NoStackTraceResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "CAPTCHA validation failed", null);
        }
    }
}
