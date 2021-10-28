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

public class CouldNotSaveFileException extends Exception {
    private static final long serialVersionUID = 1L;

    public CouldNotSaveFileException() {
        super();
    }

    public CouldNotSaveFileException(String message) {
        super(message);
    }

    public CouldNotSaveFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouldNotSaveFileException(Throwable cause) {
        super(cause);
    }
}
