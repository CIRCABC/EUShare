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

import com.circabc.easyshare.model.FileInfoRecipient;
import com.circabc.easyshare.model.FileInfoUploader;
import com.circabc.easyshare.model.Status;
import com.circabc.easyshare.model.UserInfo;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-05-17T14:19:30.497+02:00[Europe/Paris]")

@Validated
@Api(value = "user", description = "the user API")
public interface UserApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @ApiOperation(value = "", nickname = "getFilesFileInfoRecipient", notes = "Used by the INTERNAL users in order to search the files they have recieved", response = FileInfoRecipient.class, responseContainer = "List", authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Users", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns the files shared with the authenticated user by pageSize and pageNumber", response = FileInfoRecipient.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @RequestMapping(value = "/user/{userID}/files/fileInfoRecipient",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<FileInfoRecipient>> getFilesFileInfoRecipient(@ApiParam(value = "The id of the user",required=true) @PathVariable("userID") String userID,@NotNull @ApiParam(value = "Number of files returned", required = true) @Valid @RequestParam(value = "pageSize", required = true) Integer pageSize,@NotNull @ApiParam(value = "Page number", required = true) @Valid @RequestParam(value = "pageNumber", required = true) Integer pageNumber) {
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


    @ApiOperation(value = "", nickname = "getFilesFileInfoUploader", notes = "Used by the INTERNAL users in order to search their own files' FileInfoUploader", response = FileInfoUploader.class, responseContainer = "List", authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Users", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns the files shared with the authenticated user by pageSize and pageNumber. For each RecipientWithLink, the id is required !", response = FileInfoUploader.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @RequestMapping(value = "/user/{userID}/files/fileInfoUploader",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    default ResponseEntity<List<FileInfoUploader>> getFilesFileInfoUploader(@ApiParam(value = "The id of the user",required=true) @PathVariable("userID") String userID,@NotNull @ApiParam(value = "Number of files returned", required = true) @Valid @RequestParam(value = "pageSize", required = true) Integer pageSize,@NotNull @ApiParam(value = "Page number", required = true) @Valid @RequestParam(value = "pageNumber", required = true) Integer pageNumber) {
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


    @ApiOperation(value = "", nickname = "putUserUserInfo", notes = "Used by the administrators in order to update a specific INTERNAL user total space or admin status", response = UserInfo.class, authorizations = {
        @Authorization(value = "basicAuth")
    }, tags={ "Users", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns the UserInfo of the selected user", response = UserInfo.class),
        @ApiResponse(code = 400, message = "BAD REQUEST the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 401, message = "Unauthorized the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized, will be sent before 404", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @RequestMapping(value = "/user/{userID}/userInfo",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    default ResponseEntity<UserInfo> putUserUserInfo(@ApiParam(value = "The id of the user",required=true) @PathVariable("userID") String userID,@ApiParam(value = "" ,required=true )  @Valid @RequestBody UserInfo userInfo) {
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
