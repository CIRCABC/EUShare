/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.configuration;

import org.apache.commons.fileupload.FileUpload;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

// fix potential DoS attack
// https://security.snyk.io/vuln/SNYK-JAVA-COMMONSFILEUPLOAD-3326457?utm_medium=Partner&utm_source=RedHat&utm_campaign=Code-Ready-Analytics-2020&utm_content=vuln%2FSNYK-JAVA-COMMONSFILEUPLOAD-3326457
public class SafeCommonsMultipartResolver extends CommonsMultipartResolver {

  private long fileCountMax;

  public void setFileCountMax(final long fileCountMax) {
    this.fileCountMax = fileCountMax;
  }

  protected FileUpload prepareFileUpload(@Nullable String encoding) {
    FileUpload actualFileUpload = super.prepareFileUpload(encoding);
    actualFileUpload.setFileCountMax(fileCountMax);
    return actualFileUpload;
  }
}
