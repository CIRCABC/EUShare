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

import eu.europa.circabc.eushare.model.Status;
import eu.europa.circabc.eushare.model.UserInfo;
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
@Api(value = "users", description = "the users API")
public interface UsersApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /users/userInfo
     * Used by the administrators in order to search for INTERNAL users&#39; UserInfo
     *
     * @param pageSize Number of persons returned (required)
     * @param pageNumber Page number (required)
     * @param searchString  (required)
     * @param sortBy Sort by criteria (optional)
     * @return SUCCESS Returns a pageSize number of UserInfos corresponding to the searchString and the pageNumber for internal users Users (status code 200)
     *         or BAD REQUEST the Error Message will be empty (status code 400)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized (status code 403)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "getUsersUserInfo", notes = "Used by the administrators in order to search for INTERNAL users' UserInfo", response = UserInfo.class, responseContainer = "List", authorizations = {
         }, tags={ "Users", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns a pageSize number of UserInfos corresponding to the searchString and the pageNumber for internal users Users", response = UserInfo.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "BAD REQUEST the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @GetMapping(
        value = "/users/userInfo",
        produces = { "application/json" }
    )
    default ResponseEntity<List<UserInfo>> getUsersUserInfo(@NotNull @ApiParam(value = "Number of persons returned", required = true) @Valid @RequestParam(value = "pageSize", required = true) Integer pageSize,@NotNull @ApiParam(value = "Page number", required = true) @Valid @RequestParam(value = "pageNumber", required = true) Integer pageNumber,@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "searchString", required = true) String searchString,@ApiParam(value = "Sort by criteria") @Valid @RequestParam(value = "sortBy", required = false) String sortBy) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "null";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
