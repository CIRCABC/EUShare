package eu.europa.circabc.eushare.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import eu.europa.circabc.eushare.CaptchaApiImpl;
import eu.europa.circabc.eushare.configuration.EushareConfiguration;


// ... Other imports ...

@Aspect
@Component
public class CaptchaAspect {

    @Autowired
    private EushareConfiguration esConfig;

    @Before("@annotation(eu.europa.circabc.eushare.api.Captcha) && args(captchaId, captchaToken, captchaText, ..)")
    public void checkCaptcha(String captchaId, String captchaToken, String captchaText) {

        if (captchaToken == null || captchaId == null || captchaText == null) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "CAPTCHA parameters missing", null);
        }

        CaptchaApiImpl captchaApi = new CaptchaApiImpl();
        captchaApi.setCaptchaUrl(esConfig.getCaptchaUrl());

        boolean isValid = captchaApi.validate(captchaToken, captchaId, captchaText);

        if (!isValid) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "CAPTCHA validation failed", null);
        }
    }
}
