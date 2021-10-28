/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package eu.europa.circabc.eushare.exceptions;

public class WrongEmailStructureException extends Exception {

    private static final long serialVersionUID = 1L;

    public WrongEmailStructureException() {
        super();
    }

    public WrongEmailStructureException(String message) {
        super(message);
    }

    public WrongEmailStructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongEmailStructureException(Throwable cause) {
        super(cause);
    }
    
}