/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.error;

import com.circabc.easyshare.model.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpErrorAnswerBuilder {
    public static Status build400Empty() {
        return buildEmpty(400);
    }

    private static Status build401Empty() {
        return buildEmpty(401);
    }
    
    private static Status build403NotAuthorized() {
        return buildWithMessage(403, "NotAuthorized");
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

    public static String build404EmptyToString() {
        return mapAStatus(build404Empty());
    }

    public static String build404FileNotFoundToString() {
        return mapAStatus(build404FileNotFound());
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
        } catch (JsonProcessingException e) {
            // ignore
            return "";
        }
    }

}