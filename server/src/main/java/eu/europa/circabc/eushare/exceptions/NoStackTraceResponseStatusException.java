/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoStackTraceResponseStatusException extends ResponseStatusException {

    public NoStackTraceResponseStatusException(HttpStatus status) {
        super(status);
    }

    public NoStackTraceResponseStatusException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public NoStackTraceResponseStatusException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; 
    }
}
