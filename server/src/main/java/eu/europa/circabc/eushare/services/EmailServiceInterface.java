/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.services;

import eu.europa.circabc.eushare.model.FileBasics;
import eu.europa.circabc.eushare.model.FileInfoRecipient;
import java.time.LocalDate;
import javax.mail.MessagingException;

public interface EmailServiceInterface {
  public void sendDownloadNotification(
    String recipient,
    String downloaderId,
    FileBasics fileInfo
  ) throws MessagingException;

  public void sendFileDeletedNotification(
    String recipient,
    FileBasics fileInfo,
    String reason
  ) throws MessagingException;

  public void sendShareNotification(
    String recipient,
    FileInfoRecipient fileInfo,
    String message,
    String shorturl,
    LocalDate expDate
  ) throws MessagingException;
}
