/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.utils;

import eu.europa.circabc.eushare.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

  @Test
  public void testEmail() {
    Assert.assertEquals(
      true,
      StringUtils.validateEmailAddress("email@email.com")
    );
    Assert.assertEquals(
      true,
      StringUtils.validateEmailAddress("admin@admin.com")
    );
    Assert.assertEquals(
      true,
      StringUtils.validateEmailAddress("--------admin@admin.com")
    );
  }

  @Test
  public void testGivenName() {
    Assert.assertEquals(
      "email",
      StringUtils.emailToGivenName("email@email.com")
    );
    Assert.assertEquals(
      "FirstName LastName",
      StringUtils.emailToGivenName("FirstName.LastName@email.com")
    );
  }
}
