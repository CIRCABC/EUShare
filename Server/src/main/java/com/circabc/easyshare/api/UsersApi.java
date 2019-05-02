/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (4.0.0-SNAPSHOT).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.circabc.easyshare.api;

import com.circabc.easyshare.model.Status;
import com.circabc.easyshare.model.UserInfo;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-04-10T14:56:31.271+02:00[Europe/Paris]")

@Validated
@Api(value = "users", description = "the users API")
public interface UsersApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @ApiOperation(value = "", nickname = "getUsers", notes = "", response = UserInfo.class, responseContainer = "List", authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Users", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns a pageSize number of UserInfos corresponding to the searchString and the pageNumber", response = UserInfo.class, responseContainer = "List"),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @RequestMapping(value = "/users",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<UserInfo>> getUsers(@NotNull @ApiParam(value = "Number of persons returned", required = true) @Valid @RequestParam(value = "pageSize", required = true) Integer pageSize,@NotNull @ApiParam(value = "Page number", required = true) @Valid @RequestParam(value = "pageNumber", required = true) Integer pageNumber,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "searchString", required = true) String searchString) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    ApiUtil.setExampleResponse(request, "application/json", "null");
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
