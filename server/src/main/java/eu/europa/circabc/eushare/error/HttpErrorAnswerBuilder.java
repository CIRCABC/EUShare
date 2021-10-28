/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package eu.europa.circabc.eushare.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europa.circabc.eushare.model.Status;

public class HttpErrorAnswerBuilder {

    private HttpErrorAnswerBuilder() {

    }

    public static Status build400Empty() {
        return buildEmpty(400);
    }

    private static Status build401Empty() {
        return buildEmpty(401);
    }
    
    private static Status build403NotAuthorized() {
        return buildWithMessage(403, "NotAuthorized");
    }

    private static Status build403FileLargerThanAllocation() {
        return buildWithMessage(403, "FileLargerThanAllocation");
    }

    private static Status build403InssuficientMemoryLeft() {
        return buildWithMessage(403, "InssuficientMemoryLeft");
    }

    private static Status build403IllegalFileSize() {
        return buildWithMessage(403, "IllegalFileSize");
    }

    private static Status build403DateLiesInPast() {
        return buildWithMessage(403, "DateLiesInPast");
    }

    private static Status build403UserHasInsufficientSpace() {
        return buildWithMessage(403, "UserHasInsufficientSpace");
    }

    private static Status build403EmptyFileName() {
        return buildWithMessage(403, "EmptyFileName");
    }

    private static Status build403WrongEmailStructure() {
        return buildWithMessage(403, "WrongEmailStructure");
    }

    private static Status build403WrongNameStructure() {
        return buildWithMessage(403, "WrongNameStructure");
    }

    private static Status build403MessageTooLong() {
        return buildWithMessage(403, "MessageTooLong");
    }

    private static Status build404Empty() {
        return buildEmpty(404);
    }

    private static Status build404FileNotFound() {
        return buildWithMessage(404, "FileNotFound");
    }

    private static Status build404UserNotFound() {
        return buildWithMessage(404, "UserNotFound");
    }


    private static Status build500Empty() {
        return buildEmpty(500);
    }

    private static Status build500MediaTypeNotSupported() {
        return buildWithMessage(500, "MediaTypeNotSupported");
    }

    private static Status buildEmpty(int number) {
        Status status = new Status();
        status.setCode(number);
        return status;
    }

    private static Status buildWithMessage(int number, String message){
        Status status = buildEmpty(number);
        status.setMessage(message);
        return status;
    }

    public static String build400EmptyToString() {
        return mapAStatus(build400Empty());
    }

    public static String build401EmptyToString() {
        return mapAStatus(build401Empty());
    }

    public static String build403NotAuthorizedToString() {
        return mapAStatus(build403NotAuthorized());
    }

    public static String build403InssuficientMemoryLeftToString() {
        return mapAStatus(build403InssuficientMemoryLeft());
    }

    public static String build403IllegalFileSizeToString() {
        return mapAStatus(build403IllegalFileSize());
    }

    public static String build403DateLiesInPastToString() {
        return mapAStatus(build403DateLiesInPast());
    }

    public static String build403EmptyFileNameToString() {
        return mapAStatus(build403EmptyFileName());
    }

    public static String build403WrongEmailStructureToString() {
        return mapAStatus(build403WrongEmailStructure());
    }

    public static String build403WrongNameStructureToString() {
        return mapAStatus(build403WrongNameStructure());
    }

    public static String build403MessageTooLongToString() {
        return mapAStatus(build403MessageTooLong());
    }

    public static String build403FileLargerThanAllocationToString() {
        return mapAStatus(build403FileLargerThanAllocation());
    }

    public static String build404EmptyToString() {
        return mapAStatus(build404Empty());
    }

    public static String build404FileNotFoundToString() {
        return mapAStatus(build404FileNotFound());
    }

    public static String build403UserHasInsufficientSpaceToString() {
        return mapAStatus(build403UserHasInsufficientSpace());
    }

    public static String build404UserNotFoundToString() {
        return mapAStatus(build404UserNotFound());
    }

    public static String build500EmptyToString() {
        return mapAStatus(build500Empty());
    }

    public static String build500MediaTypeNotSupportedToString() {
        return mapAStatus(build500MediaTypeNotSupported());
    }

    private static String mapAStatus(Status status) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(status);
        } catch (JsonProcessingException e) {//NOSONAR
            // ignore
            return "";
        }
    }

}