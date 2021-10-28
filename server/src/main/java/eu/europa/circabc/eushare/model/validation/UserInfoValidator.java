
/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.model.validation;

import eu.europa.circabc.eushare.model.UserInfo;

public class UserInfoValidator {
    private UserInfoValidator() {

    }

    public static boolean validate(UserInfo userInfo) {
        if (userInfo == null) {
            return false;
        }
        if (userInfo.getTotalSpace() == null) {// NOSONAR
            return false;
        }
        if (userInfo.getUsedSpace() == null) {// NOSONAR
            return false;
        }
        if (userInfo.getId() == null) {// NOSONAR
            return false;
        }
        if (userInfo.getLoginUsername() == null) {// NOSONAR
            return false;
        }
        if (userInfo.getGivenName() == null) {// NOSONAR
            return false;
        }
        if (userInfo.getIsAdmin() == null) {// NOSONAR
            return false;
        }
        return true;
    }
}