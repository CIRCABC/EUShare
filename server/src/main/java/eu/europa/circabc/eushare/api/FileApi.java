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

import eu.europa.circabc.eushare.model.FileBasics;
import eu.europa.circabc.eushare.model.FileInfoUploader;
import eu.europa.circabc.eushare.model.FileRequest;
import eu.europa.circabc.eushare.model.FileResult;
import eu.europa.circabc.eushare.model.FileStatusUpdate;
import eu.europa.circabc.eushare.model.Recipient;
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
@Api(value = "file", description = "the file API")
public interface FileApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * DELETE /file/{fileID}
     * Used by INTERNAL users and ADMIN in order to delete a file
     *
     * @param fileID The id of the file (required)
     * @param reason Reason for deletion of the file (optional)
     * @return SUCCESS Deletes the file content and its meta data, also sends an email to original uploader with the reason of deletion if the reason is given (status code 200)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized (status code 403)
     *         or NOT FOUND the Error Message will be empty (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "deleteFile", notes = "Used by INTERNAL users and ADMIN in order to delete a file", authorizations = {
         }, tags={ "File", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Deletes the file content and its meta data, also sends an email to original uploader with the reason of deletion if the reason is given"),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @DeleteMapping(
        value = "/file/{fileID}",
        produces = { "application/json" }
    )
    default ResponseEntity<Void> deleteFile(@ApiParam(value = "The id of the file",required=true) @PathVariable("fileID") String fileID,@ApiParam(value = "Reason for deletion of the file") @Valid @RequestParam(value = "reason", required = false) String reason) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * DELETE /file/{fileID}/fileRequest/sharedWith
     * Used by INTERNAL users in order to delete a share link for one of the shared users
     *
     * @param fileID The id of the file (required)
     * @param userID The id of the user (required)
     * @return SUCCESS (status code 200)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized (status code 403)
     *         or NOT FOUND the Error Message will be either FileNotFound or UserNotFound (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "deleteFileSharedWithUser", notes = "Used by INTERNAL users in order to delete a share link for one of the shared users", authorizations = {
         }, tags={ "File", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS"),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be either FileNotFound or UserNotFound", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @DeleteMapping(
        value = "/file/{fileID}/fileRequest/sharedWith",
        produces = { "application/json" }
    )
    default ResponseEntity<Void> deleteFileSharedWithUser(@ApiParam(value = "The id of the file",required=true) @PathVariable("fileID") String fileID,@NotNull @ApiParam(value = "The id of the user", required = true) @Valid @RequestParam(value = "userID", required = true) String userID) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /file/{fileID}
     * Used by INTERNAL and EXTERNAL users to download a shared file
     *
     * @param fileID The id of the file (required)
     * @param password Password of the file (optional)
     * @return SUCCESS Returns the file and sends a mail to original uploader to inform him of the download (status code 200)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or NOT FOUND the Error Message will be empty (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "getFile", notes = "Used by INTERNAL and EXTERNAL users to download a shared file", response = org.springframework.core.io.Resource.class, tags={ "File", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns the file and sends a mail to original uploader to inform him of the download", response = org.springframework.core.io.Resource.class),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @GetMapping(
        value = "/file/{fileID}",
        produces = { "application/octet-stream", "application/json" }
    )
    default ResponseEntity<org.springframework.core.io.Resource> getFile(@ApiParam(value = "The id of the file",required=true) @PathVariable("fileID") String fileID,@ApiParam(value = "Password of the file") @Valid @RequestParam(value = "password", required = false) String password) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /file/{fileShortUrl}/fileInfo
     * Used by INTERNAL and EXTERNAL users to get file&#39;s metadata from short url
     *
     * @param fileShortUrl The short url of the file (required)
     * @return SUCCESS Returns the File&#39;s metadata of the newly-created file (status code 200)
     *         or BAD REQUEST the Error Message will be empty (status code 400)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized, IllegalFileSize, DateLiesInPast, UserHasInsufficientSpace, EmptyFileName, WrongEmailStructure, WrongNameStructure, MessageTooLong (status code 403)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "getFileInfo", notes = "Used by INTERNAL and EXTERNAL users to get file's metadata from short url", response = FileBasics.class, tags={ "File", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns the File's metadata of the newly-created file", response = FileBasics.class),
        @ApiResponse(code = 400, message = "BAD REQUEST the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized, IllegalFileSize, DateLiesInPast, UserHasInsufficientSpace, EmptyFileName, WrongEmailStructure, WrongNameStructure, MessageTooLong", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @GetMapping(
        value = "/file/{fileShortUrl}/fileInfo",
        produces = { "application/json" }
    )
    default ResponseEntity<FileBasics> getFileInfo(@ApiParam(value = "The short url of the file",required=true) @PathVariable("fileShortUrl") String fileShortUrl) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"size\" : 0.08008281904610115, \"name\" : \"name\", \"hasPassword\" : true, \"expirationDate\" : \"2000-01-23\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * POST /file/{fileID}/fileRequest/fileContent
     * Used by INTERNAL users in order to post the file content on the pre-reserved file space
     *
     * @param fileID The id of the file (required)
     * @param file  (optional)
     * @return SUCCESS Returns the FileInfoUploader of the uploaded file (status code 200)
     *         or BAD REQUEST the Error Message will be empty (status code 400)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized, FileLargerThanAllocation, IllegalFileSize (status code 403)
     *         or NOT FOUND the Error Message will be empty (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "postFileContent", notes = "Used by INTERNAL users in order to post the file content on the pre-reserved file space", response = FileInfoUploader.class, authorizations = {
         }, tags={ "File", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns the FileInfoUploader of the uploaded file", response = FileInfoUploader.class),
        @ApiResponse(code = 400, message = "BAD REQUEST the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized, FileLargerThanAllocation, IllegalFileSize", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @PostMapping(
        value = "/file/{fileID}/fileRequest/fileContent",
        produces = { "application/json" },
        consumes = { "multipart/form-data" }
    )
    default ResponseEntity<FileInfoUploader> postFileContent(@ApiParam(value = "The id of the file",required=true) @PathVariable("fileID") String fileID,@ApiParam(value = "") @Valid @RequestPart(value = "file", required = false) MultipartFile file) {
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


    /**
     * POST /file/fileRequest
     * Used by INTERNAL users in order to request the reservation of space for a file
     *
     * @param fileRequest  (required)
     * @return SUCCESS Returns the File ID of the newly-created file (status code 200)
     *         or BAD REQUEST the Error Message will be empty (status code 400)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized, IllegalFileSize, DateLiesInPast, UserHasInsufficientSpace, EmptyFileName, WrongEmailStructure, WrongNameStructure, MessageTooLong (status code 403)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "postFileFileRequest", notes = "Used by INTERNAL users in order to request the reservation of space for a file", response = FileResult.class, tags={ "File", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns the File ID of the newly-created file", response = FileResult.class),
        @ApiResponse(code = 400, message = "BAD REQUEST the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized, IllegalFileSize, DateLiesInPast, UserHasInsufficientSpace, EmptyFileName, WrongEmailStructure, WrongNameStructure, MessageTooLong", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @PostMapping(
        value = "/file/fileRequest",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<FileResult> postFileFileRequest(@ApiParam(value = "" ,required=true )  @Valid @RequestBody FileRequest fileRequest) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"fileId\" : \"fileId\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * POST /file/{fileID}/fileRequest/sharedWith
     * Used by INTERNAL users in order to add a person to the list of shared, after having uploaded the file a first time. Will send an email if required
     *
     * @param fileID The id of the file (required)
     * @param recipient The userID or email of user to share the file with (required)
     * @return SUCCESS Returns the Recipient for the added recipient (status code 200)
     *         or BAD REQUEST the Error Message will be empty (status code 400)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized, WrongEmailStructure, WrongNameStructure, MessageTooLong (status code 403)
     *         or NOT FOUND the Error Message will be empty (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "postFileSharedWith", notes = "Used by INTERNAL users in order to add a person to the list of shared, after having uploaded the file a first time. Will send an email if required", response = Recipient.class, authorizations = {
         }, tags={ "File", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns the Recipient for the added recipient", response = Recipient.class),
        @ApiResponse(code = 400, message = "BAD REQUEST the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized, WrongEmailStructure, WrongNameStructure, MessageTooLong", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @PostMapping(
        value = "/file/{fileID}/fileRequest/sharedWith",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<Recipient> postFileSharedWith(@ApiParam(value = "The id of the file",required=true) @PathVariable("fileID") String fileID,@ApiParam(value = "The userID or email of user to share the file with" ,required=true )  @Valid @RequestBody Recipient recipient) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"downloadNotification\" : true, \"downloadLink\" : \"downloadLink\", \"shortUrl\" : \"shortUrl\", \"message\" : \"message\", \"email\" : \"email\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * POST /file/{fileID}/fileRequest/sharedWithDownloadNotification
     *
     * @param fileID The id of the file (required)
     * @param userEmail The email of the user (required)
     * @param downloadNotification true if download notification has to be sent (required)
     * @return SUCCESS (status code 200)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized (status code 403)
     *         or NOT FOUND the Error Message will be either FileNotFound or UserNotFound (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "postFileSharedWithDownloadNotification", notes = "", authorizations = {
         }, tags={ "File", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS"),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be either FileNotFound or UserNotFound", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @PostMapping(
        value = "/file/{fileID}/fileRequest/sharedWithDownloadNotification",
        produces = { "application/json" }
    )
    default ResponseEntity<Void> postFileSharedWithDownloadNotification(@ApiParam(value = "The id of the file",required=true) @PathVariable("fileID") String fileID,@NotNull @ApiParam(value = "The email of the user", required = true) @Valid @RequestParam(value = "userEmail", required = true) String userEmail,@NotNull @ApiParam(value = "true if download notification has to be sent", required = true) @Valid @RequestParam(value = "downloadNotification", required = true) Boolean downloadNotification) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * POST /file/{fileID}/fileRequest/sharedWithReminder
     *
     * @param fileID The id of the file (required)
     * @param userEmail The email of the user (required)
     * @return SUCCESS (status code 200)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized (status code 403)
     *         or NOT FOUND the Error Message will be either FileNotFound or UserNotFound (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "postFileSharedWithReminder", notes = "", authorizations = {
         }, tags={ "File", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS"),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be either FileNotFound or UserNotFound", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @PostMapping(
        value = "/file/{fileID}/fileRequest/sharedWithReminder",
        produces = { "application/json" }
    )
    default ResponseEntity<Void> postFileSharedWithReminder(@ApiParam(value = "The id of the file",required=true) @PathVariable("fileID") String fileID,@NotNull @ApiParam(value = "The email of the user", required = true) @Valid @RequestParam(value = "userEmail", required = true) String userEmail) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /file/{fileID}
     * Used by ADMIN to update file&#39;s metadata (expiration date)
     *
     * @param fileID The id of the file (required)
     * @param fileBasics  (required)
     * @return SUCCESS Updates the file content and its meta data (status code 200)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized (status code 403)
     *         or NOT FOUND the Error Message will be empty (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "updateFile", notes = "Used by ADMIN to update file's metadata (expiration date)", authorizations = {
         }, tags={ "File", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Updates the file content and its meta data"),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @PutMapping(
        value = "/file/{fileID}",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<Void> updateFile(@ApiParam(value = "The id of the file",required=true) @PathVariable("fileID") String fileID,@ApiParam(value = "" ,required=true )  @Valid @RequestBody FileBasics fileBasics) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * PUT /file/{fileID}/status : Update file status
     *
     * @param fileID ID of the file to be updated (required)
     * @param fileStatusUpdate Status update payload (required)
     * @return File status updated successfully (status code 200)
     *         or Invalid request body (status code 400)
     *         or File not found (status code 404)
     */
    @ApiOperation(value = "Update file status", nickname = "updateStatus", notes = "", tags={ "File", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "File status updated successfully"),
        @ApiResponse(code = 400, message = "Invalid request body"),
        @ApiResponse(code = 404, message = "File not found") })
    @PutMapping(
        value = "/file/{fileID}/status",
        consumes = { "application/json" }
    )
    default ResponseEntity<Void> updateStatus(@ApiParam(value = "ID of the file to be updated",required=true) @PathVariable("fileID") String fileID,@ApiParam(value = "Status update payload" ,required=true )  @Valid @RequestBody FileStatusUpdate fileStatusUpdate) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
