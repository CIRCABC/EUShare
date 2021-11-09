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

import eu.europa.circabc.eushare.model.Status;
import eu.europa.circabc.eushare.model.UserResult;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Api(value = "login", description = "the login API")
public interface LoginApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();  
    }

    /**
     * POST /login
     * Used to login by internal users
     *
     * @return Returns the user id to use in all other requests (status code 200)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "postLogin", notes = "Used to login by internal users", response = UserResult.class, authorizations = {
         }, tags={ "Session", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Returns the user id to use in all other requests", response = UserResult.class),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @PostMapping(
        value = "/login",
        produces = { "application/json" }
    )
    default ResponseEntity<UserResult> postLogin() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"userId\" : \"userId\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
