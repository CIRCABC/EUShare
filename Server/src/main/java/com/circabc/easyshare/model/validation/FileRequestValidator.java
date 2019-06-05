/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package com.circabc.easyshare.model.validation;

import com.circabc.easyshare.model.FileRequest;
import com.circabc.easyshare.model.Recipient;

public class FileRequestValidator {
    private FileRequestValidator(){

    }

    public static boolean validate(FileRequest fileRequest) { //NOSONAR
        if (fileRequest == null) {
            return false;
        }
        if (fileRequest.getExpirationDate() == null) { 
            return false;
        }
        if (fileRequest.getHasPassword() == null) {
            return false;
        } 
        if (fileRequest.getHasPassword() && fileRequest.getPassword() == null) {
            return false;
        }
        if (fileRequest.getName() == null) {
            return false;
        }
        if (fileRequest.getSize() == null) { 
            return false;
        }
        if (fileRequest.getSharedWith() == null) {
            return false;
        }
        if (fileRequest.getSharedWith().isEmpty()) { 
            return false;
        }
        for(Recipient recipient : fileRequest.getSharedWith()) {
            if (recipient.getEmailOrName() == null) {
                return false;
            }
            if (recipient.getSendEmail() == null) {
                return false;
            }
            if (recipient.getSendEmail() && recipient.getMessage() == null) {
                return false;
            }
        }
        return true;
    }

}