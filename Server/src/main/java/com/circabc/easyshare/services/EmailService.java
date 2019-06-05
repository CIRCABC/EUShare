/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.circabc.easyshare.model.FileBasics;
import com.circabc.easyshare.model.FileInfoRecipient;

/**
 * Service for sending emails from EasyShare
 */
@Service
public class EmailService {
    private static final String DOWNLOADER = "downloader";
    private static final String FILENAME = "filename";
    private static final String UPLOADER = "uploader";

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private TemplateEngine templateEngine;

    private void sendMessage(String recipient, String content) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(sender.createMimeMessage(), "UTF-8");

        helper.setTo(recipient);
        helper.setText(content, true);
        sender.send(message);
    }

    /**
     * Send notification to {@code recipient} that {@code downloaderId} downloaded a file.
     */
    public void sendDownloadNotification(String recipient, String downloaderId, FileBasics fileInfo) throws MessagingException {
        Context ctx = new Context();
        ctx.setVariable(DOWNLOADER, downloaderId);
        ctx.setVariable(FILENAME, fileInfo.getName());
        String content = this.templateEngine.process("mail/html/download-notification", ctx);
        sendMessage(recipient, content);
    }

    /**
     * Send notification to {@code recipient} that one of his files got deleted.
     */
    public void sendFileDeletedNotification(String recipient, FileBasics fileInfo, String reason) throws MessagingException {
        Context ctx = new Context();
        ctx.setVariable(FILENAME, fileInfo.getName());
        ctx.setVariable("reason", reason);
        String content = this.templateEngine.process("mail/html/delete-notification", ctx);

        sendMessage(recipient, content);
    }

    /**
     * Send notification to {@code recipient} that someone shared a file with him.
     */
    public void sendShareNotification(String recipient, FileInfoRecipient fileInfo, String message) throws MessagingException {
        Context ctx = new Context();
        ctx.setVariable(FILENAME, fileInfo.getName());
        ctx.setVariable(UPLOADER, fileInfo.getUploaderName());
        ctx.setVariable("message", message);
        String content = this.templateEngine.process("mail/html/share-notification", ctx);

        sendMessage(recipient, content);
    }
}
