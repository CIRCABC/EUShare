/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package com.circabc.easyshare.utils;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void testEmail() {
        Assert.assertEquals(true, StringUtils.validateEmailAddress("email@email.com"));
        Assert.assertEquals(true, StringUtils.validateEmailAddress("admin@admin.com"));
        Assert.assertEquals(true, StringUtils.validateEmailAddress("--------admin@admin.com"));
    }
}