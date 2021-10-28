/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.0.1).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package eu.europa.circabc.eushare.api;

import eu.europa.circabc.eushare.model.FileInfoRecipient;
import eu.europa.circabc.eushare.model.FileInfoUploader;
import eu.europa.circabc.eushare.model.Status;
import eu.europa.circabc.eushare.model.UserInfo;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.Pageable;
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
@Api(value = "user", description = "the user API")
public interface UserApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /user/{userID}/files/fileInfoRecipient
     * Used by the INTERNAL users in order to search the files they have recieved
     *
     * @param userID The id of the user (required)
     * @param pageSize Number of files returned (required)
     * @param pageNumber Page number (required)
     * @return SUCCESS Returns the files shared with the authenticated user by pageSize and pageNumber (status code 200)
     *         or BAD REQUEST the Error Message will be empty (status code 400)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized (status code 403)
     *         or NOT FOUND the Error Message will be empty (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "getFilesFileInfoRecipient", notes = "Used by the INTERNAL users in order to search the files they have recieved", response = FileInfoRecipient.class, responseContainer = "List", authorizations = {
         }, tags={ "Users", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns the files shared with the authenticated user by pageSize and pageNumber", response = FileInfoRecipient.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "BAD REQUEST the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @GetMapping(
        value = "/user/{userID}/files/fileInfoRecipient",
        produces = { "application/json" }
    )
    default ResponseEntity<List<FileInfoRecipient>> getFilesFileInfoRecipient(@ApiParam(value = "The id of the user",required=true) @PathVariable("userID") String userID,@NotNull @ApiParam(value = "Number of files returned", required = true) @Valid @RequestParam(value = "pageSize", required = true) Integer pageSize,@NotNull @ApiParam(value = "Page number", required = true) @Valid @RequestParam(value = "pageNumber", required = true) Integer pageNumber) {
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
     * GET /user/{userID}/files/fileInfoUploader
     * Used by the INTERNAL users in order to search their own files&#39; FileInfoUploader
     *
     * @param userID The id of the user (required)
     * @param pageSize Number of files returned (required)
     * @param pageNumber Page number (required)
     * @return SUCCESS Returns the files shared with the authenticated user by pageSize and pageNumber. (status code 200)
     *         or BAD REQUEST the Error Message will be empty (status code 400)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized (status code 403)
     *         or NOT FOUND the Error Message will be empty (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "getFilesFileInfoUploader", notes = "Used by the INTERNAL users in order to search their own files' FileInfoUploader", response = FileInfoUploader.class, responseContainer = "List", authorizations = {
         }, tags={ "Users", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns the files shared with the authenticated user by pageSize and pageNumber.", response = FileInfoUploader.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "BAD REQUEST the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @GetMapping(
        value = "/user/{userID}/files/fileInfoUploader",
        produces = { "application/json" }
    )
    default ResponseEntity<List<FileInfoUploader>> getFilesFileInfoUploader(@ApiParam(value = "The id of the user",required=true) @PathVariable("userID") String userID,@NotNull @ApiParam(value = "Number of files returned", required = true) @Valid @RequestParam(value = "pageSize", required = true) Integer pageSize,@NotNull @ApiParam(value = "Page number", required = true) @Valid @RequestParam(value = "pageNumber", required = true) Integer pageNumber) {
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
     * GET /user/{userID}/userInfo
     * Used by the users in order to fetch their personnal information
     *
     * @param userID The id of the user (required)
     * @return SUCCESS Returns the UserInfo of the selected user (status code 200)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized, will be sent before 404 (status code 403)
     *         or NOT FOUND the Error Message will be empty (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "getUserUserInfo", notes = "Used by the users in order to fetch their personnal information", response = UserInfo.class, authorizations = {
         }, tags={ "Users", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns the UserInfo of the selected user", response = UserInfo.class),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized, will be sent before 404", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @GetMapping(
        value = "/user/{userID}/userInfo",
        produces = { "application/json" }
    )
    default ResponseEntity<UserInfo> getUserUserInfo(@ApiParam(value = "The id of the user",required=true) @PathVariable("userID") String userID) {
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
     * PUT /user/{userID}/userInfo
     * Used by the administrators in order to update a specific INTERNAL user total space or admin status
     *
     * @param userID The id of the user (required)
     * @param userInfo  (required)
     * @return SUCCESS Returns the UserInfo of the selected user (status code 200)
     *         or BAD REQUEST the Error Message will be empty (status code 400)
     *         or UNAUTHORIZED the Error message will be empty (status code 401)
     *         or FORBIDDEN the Error message will be NotAuthorized, will be sent before 404 (status code 403)
     *         or NOT FOUND the Error Message will be empty (status code 404)
     *         or INTERNAL SERVER ERROR the Error Message will be empty (status code 500)
     */
    @ApiOperation(value = "", nickname = "putUserUserInfo", notes = "Used by the administrators in order to update a specific INTERNAL user total space or admin status", response = UserInfo.class, authorizations = {
         }, tags={ "Users", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "SUCCESS Returns the UserInfo of the selected user", response = UserInfo.class),
        @ApiResponse(code = 400, message = "BAD REQUEST the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 401, message = "UNAUTHORIZED the Error message will be empty", response = Status.class),
        @ApiResponse(code = 403, message = "FORBIDDEN the Error message will be NotAuthorized, will be sent before 404", response = Status.class),
        @ApiResponse(code = 404, message = "NOT FOUND the Error Message will be empty", response = Status.class),
        @ApiResponse(code = 500, message = "INTERNAL SERVER ERROR the Error Message will be empty", response = Status.class) })
    @PutMapping(
        value = "/user/{userID}/userInfo",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<UserInfo> putUserUserInfo(@ApiParam(value = "The id of the user",required=true) @PathVariable("userID") String userID,@ApiParam(value = "" ,required=true )  @Valid @RequestBody UserInfo userInfo) {
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