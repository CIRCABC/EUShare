/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.model.validation;

import eu.europa.circabc.eushare.model.FileRequest;
import eu.europa.circabc.eushare.model.Recipient;

public class FileRequestValidator {
    private FileRequestValidator() {

    }

    public static boolean validate(FileRequest fileRequest) { // NOSONAR
        if (fileRequest == null) {// NOSONAR
            return false;
        }
        if (fileRequest.getExpirationDate() == null) { // NOSONAR
            return false;
        }
        if (fileRequest.getHasPassword() == null) {// NOSONAR
            return false;
        }
        if (fileRequest.getHasPassword() && fileRequest.getPassword() == null) {// NOSONAR
            return false;
        }
        if (fileRequest.getName() == null) {// NOSONAR
            return false;
        }
        if (fileRequest.getSize() == null) { // NOSONAR
            return false;
        }
        if (fileRequest.getSharedWith() == null) {// NOSONAR
            return false;
        }
        if (fileRequest.getSharedWith().isEmpty()) { // NOSONAR
            return false;
        }
        for (Recipient recipient : fileRequest.getSharedWith()) { //NOSONAR
            if (!RecipientValidator.validate(recipient)) { //NOSONAR
                return false;
            }
        }
        return true;
    }

}