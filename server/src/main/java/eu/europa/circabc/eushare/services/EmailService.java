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

import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import eu.europa.circabc.eushare.configuration.EushareConfiguration;
import eu.europa.circabc.eushare.model.FileBasics;
import eu.europa.circabc.eushare.model.FileInfoRecipient;

/**
 * Service for sending emails from EasyShare
 */
@Service
public class EmailService implements EmailServiceInterface {

    private Logger log = LoggerFactory.getLogger(EmailService.class);

    private static final String DOWNLOADER = "downloader";
    private static final String FILENAME = "filename";
    private static final String UPLOADER = "uploader";
    private static final String REASON = "reason";
    private static final String MESSAGE = "message";
    private static final String LINK = "link";

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private EushareConfiguration esConfig;

    private void sendMessage(String recipient, String content) throws MessagingException {
        if (esConfig.isActivateMailService()) {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(recipient);
            helper.setText(content, true);
            helper.setSubject("EUShare notification");

            sender.send(message);
        }
    }

    /**
     * Send notification to {@code recipient} that {@code downloaderId} downloaded a
     * file.
     */
    @Override
    public void sendDownloadNotification(String recipient, String downloaderId, FileBasics fileInfo)
            throws MessagingException, ConnectException {
        Context ctx = new Context();
        ctx.setVariable(DOWNLOADER, downloaderId);
        ctx.setVariable(FILENAME, fileInfo.getName());
        String content = this.templateEngine.process("mail/html/download-notification", ctx);
        this.sendMessage(recipient, content);
    }

    /**
     * Send notification to {@code recipient} that one of his files got deleted.
     */
    @Override
    public void sendFileDeletedNotification(String recipient, FileBasics fileInfo, String reason)
            throws MessagingException {
        Context ctx = new Context();
        ctx.setVariable(FILENAME, fileInfo.getName());
        ctx.setVariable(REASON, reason);
        String content = this.templateEngine.process("mail/html/delete-notification", ctx);

        this.sendMessage(recipient, content);
    }

    /**
     * Send notification to {@code recipient} that someone shared a file with him.
     */
    @Override
    public void sendShareNotification(String recipient, FileInfoRecipient fileInfo, String message)
            throws MessagingException {
        Context ctx = new Context();
        ctx.setVariable(FILENAME, fileInfo.getName());
        ctx.setVariable(UPLOADER, fileInfo.getUploaderName());
        StringBuilder sb = new StringBuilder();
        sb.append(this.esConfig.getClientHttpAddress());
        sb.append("/filelink/");
        sb.append(fileInfo.getFileId());
        sb.append("/");
        sb.append(Base64.getUrlEncoder().withoutPadding().encodeToString(fileInfo.getName().getBytes()));
        sb.append("/");
        if (fileInfo.getHasPassword()) {
            sb.append("1");
        } else {
            sb.append("0");
        }

        try {
            URI linkUri = new URI(sb.toString());
            ctx.setVariable(LINK, linkUri.toASCIIString());
        } catch (URISyntaxException e) {
            log.error(e.getReason(), e);
        }
        
        ctx.setVariable(MESSAGE, message);
        String content = this.templateEngine.process("mail/html/share-notification", ctx);
        this.sendMessage(recipient, content);
    }
}
