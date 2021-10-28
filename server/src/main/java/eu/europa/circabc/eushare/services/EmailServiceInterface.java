/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.services;

import java.net.ConnectException;

import javax.mail.MessagingException;

import eu.europa.circabc.eushare.model.FileBasics;
import eu.europa.circabc.eushare.model.FileInfoRecipient;

public interface EmailServiceInterface {
    public void sendDownloadNotification(String recipient, String downloaderId, FileBasics fileInfo)
            throws MessagingException, ConnectException;

    public void sendFileDeletedNotification(String recipient, FileBasics fileInfo, String reason)
            throws MessagingException;

    public void sendShareNotification(String recipient, FileInfoRecipient fileInfo, String message)
            throws MessagingException;
}