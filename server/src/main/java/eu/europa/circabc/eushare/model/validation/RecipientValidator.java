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
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.model.validation;

import eu.europa.circabc.eushare.model.Recipient;

public class RecipientValidator {
    private RecipientValidator() {

    }

    public static boolean validate(Recipient recipient) {
        if (recipient == null) { // NOSONAR
            return false;
        }
        if (recipient.getEmailOrName() == null) { // NOSONAR
            return false;
        }
        if (recipient.getSendEmail() == null) { // NOSONAR
            return false;
        }
        return true;
    }
}