/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.1.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package eu.europa.circabc.eushare.api;

import eu.europa.circabc.eushare.model.ApiKey;
import eu.europa.circabc.eushare.model.Status;
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
@Api(value = "apikey", description = "the apikey API")
public interface ApikeyApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /apikey
     * Generate an API-Key for user
     *
     * @return SUCCESS Returns ApiKey (status code 200)
     *         or BAD REQUEST the Error Message will be empty (status code 400)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized (status code 403)
     *         or NOT FOUND the Error Message will be empty (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "getApiKey", notes = "Generate an API-Key for user", response = ApiKey.class, tags={ "ApiKey", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns ApiKey", response = ApiKey.class),
        @ApiResponse(code = 400, message = "BAD REQUEST the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @GetMapping(
        value = "/apikey",
        produces = { "application/json" }
    )
    default ResponseEntity<ApiKey> getApiKey() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"apikey\" : \"apikey\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
