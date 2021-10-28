package eu.europa.circabc.eushare.api;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import eu.europa.circabc.eushare.error.HttpErrorAnswerBuilder;
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.services.UserService;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.easyShare.base-path:}")
public class LoginApiController implements LoginApi {

    private  Logger log = LoggerFactory.getLogger(LoginApiController.class);

    private final NativeWebRequest request;

    @Autowired
    private UserService userService;

    @org.springframework.beans.factory.annotation.Autowired
    public LoginApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<String> postLogin() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return new ResponseEntity<String>(userService.getAuthenticatedUserId(authentication), HttpStatus.OK);
        } catch (WrongAuthenticationException exc) {
            log.debug("wrong authentication !");
            ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatus.UNAUTHORIZED,
            HttpErrorAnswerBuilder.build401EmptyToString(), exc);
            throw responseStatusException;
        }
    }


}
