/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.model.AbuseReport;
import eu.europa.circabc.eushare.model.AbuseReportDetails;
import eu.europa.circabc.eushare.model.EnumConverter;
import eu.europa.circabc.eushare.security.AdminOnly;
import eu.europa.circabc.eushare.services.EmailService;
import eu.europa.circabc.eushare.services.FileService;
import eu.europa.circabc.eushare.services.UserService;
import eu.europa.circabc.eushare.storage.dto.AbuseReportDetailsDTO;
import eu.europa.circabc.eushare.storage.entity.DBAbuse;
import eu.europa.circabc.eushare.storage.entity.DBShare;
import eu.europa.circabc.eushare.storage.entity.DBUser;
import eu.europa.circabc.eushare.storage.repository.AbuseRepository;
import eu.europa.circabc.eushare.storage.repository.UserRepository;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
public class AbuseApiController implements AbuseApi {

    private static final Map<String, String> REASONS;

    static {
        REASONS = new HashMap<>();
        REASONS.put("abuse_reason_advertising_propaganda", "Advertisement or Propaganda");
        REASONS.put("abuse_reason_copyright_infringement", "Copyright Infringement");
        REASONS.put("abuse_reason_hate_violence_threats", "Hate, Violence, Threats");
        REASONS.put("abuse_reason_inappropriate_content", "Inappropriate or Illegal Content");
        REASONS.put("abuse_reason_malware_suspected", "Suspected Virus or Malware");
        REASONS.put("abuse_reason_other_issues", "Other Issues");
        REASONS.put("abuse_reason_phishing_attempt", "Phishing Attempt");
        REASONS.put("abuse_reason_spam", "Spam");
    }

    public static String getReason(String key) {
        return REASONS.get(key);
    }

    @Autowired
    private AbuseRepository abuseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserService userService;

    @Autowired
    public FileService fileService;

    @Autowired
    private EmailService emailService;

    @AdminOnly
    @Override
    public ResponseEntity<AbuseReport> getAbuseReport(String id) {

        DBAbuse dbAbuse = abuseRepository.findOneById(id);
        if (dbAbuse == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Abuse report not found");
        }

        AbuseReport abuseReport = dbAbuse.toAbuseReport();
        return ResponseEntity.ok(abuseReport);

    }

    @Override
    public ResponseEntity<AbuseReport> createAbuseReport(@Valid AbuseReport abuseReport) {

        DBAbuse dbAbuse = DBAbuse.toDBAbuse(abuseReport);

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication.isAuthenticated()) {
            String requesterId;
            try {
                requesterId = userService.getAuthenticatedUserId(authentication);
                DBUser user = userRepository.findOneById(requesterId);
                dbAbuse.setReporter(user.getEmail());
            } catch (WrongAuthenticationException e) {
                e.printStackTrace();
            }
        }

        DBShare share = fileService.findShare(dbAbuse.getFileId());

        if (dbAbuse.getFileId().length() == DBShare.SHORT_URL_LENGTH)
            dbAbuse.setShortUrl(dbAbuse.getFileId());
        else
            dbAbuse.setShortUrl(share.getShorturl());

        String file_id = share.getFile().getId();
        dbAbuse.setFileId(file_id);

        dbAbuse.setDate(LocalDate.now());
        dbAbuse.setStatus(DBAbuse.Status.WAITING);

        abuseRepository.save(dbAbuse);
        String filename = share.getFile().getFilename();
        String uploader = share.getFile().getUploader().getName() + "(" + share.getFile().getUploader().getEmail()
                + ")";

        try {
            abuseReport.setReason(getReason(abuseReport.getReason()));
            this.emailService.sendAbuseReportNotification("DIGIT-CIRCABC-SUPPORT@ec.europa.eu", filename, uploader,
                    abuseReport);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ResponseEntity.ok(abuseReport);

    }

    @AdminOnly
    @Override
    public ResponseEntity<Map<String, List<AbuseReportDetails>>> getAbuseReportList() {

        List<AbuseReportDetailsDTO> abuseReportDetailsDto = abuseRepository.findAllDetails();
        Map<String, List<AbuseReportDetails>> groupedReports = new HashMap<>();

        for (AbuseReportDetailsDTO dto : abuseReportDetailsDto) {
            AbuseReportDetails detail = dto.toAbuseReportDetails();

            String fileId = detail.getFileId();
            if (!groupedReports.containsKey(fileId)) {
                groupedReports.put(fileId, new ArrayList<>());
            }
            groupedReports.get(fileId).add(detail);
        }

        return ResponseEntity.ok(groupedReports);
    }

    @AdminOnly
    @Override
    public ResponseEntity<Void> deleteAbuseReport(@PathVariable("id") String id) {
        DBAbuse dbAbuse = abuseRepository.findOneById(id);
        String fileId = dbAbuse.getFileId();
        List<DBAbuse> dbAbuseForFile = abuseRepository.findByFileId(fileId);
        for (DBAbuse similarDbAbuse : dbAbuseForFile) {
            abuseRepository.delete(similarDbAbuse);
        }

        return ResponseEntity.ok().build();
    }

    @AdminOnly
    @Override
    public ResponseEntity<AbuseReportDetails> updateAbuseReport(@PathVariable("id") String id,
            @Valid @RequestBody AbuseReportDetails abuseReportDetails) {

        List<DBAbuse> dbAbuseForFile = abuseRepository.findByFileId(abuseReportDetails.getFileId());
        for (DBAbuse dbAbuse : dbAbuseForFile) {
            DBAbuse.Status dbAbuseStatus = EnumConverter.convert(abuseReportDetails.getStatus(),
                    DBAbuse.Status.class);
            dbAbuse.setStatus(dbAbuseStatus);
            dbAbuse = abuseRepository.save(dbAbuse);
        }

        if (abuseReportDetails.getStatus() == AbuseReportDetails.StatusEnum.APPROVED) {
            String uploader = abuseReportDetails.getUploaderEmail();

            DBUser user = userRepository.findOneByEmailIgnoreCase(uploader);
            user.setStatus(DBUser.Status.valueOf(abuseReportDetails.getUploaderStatus()));
            userRepository.save(user);

            fileService.freezeFile(abuseReportDetails.getFileId());

            try {
                abuseReportDetails.setReason(getReason(abuseReportDetails.getReason()));
                this.emailService.sendAbuseBlameNotification("DIGIT-CIRCABC-SUPPORT@ec.europa.eu", abuseReportDetails);
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        if (abuseReportDetails.getStatus() == AbuseReportDetails.StatusEnum.DENIED) {
            String uploader = abuseReportDetails.getUploaderEmail();
            DBUser user = userRepository.findOneByEmailIgnoreCase(uploader);
            user.setStatus(DBUser.Status.REGULAR);
            userRepository.save(user);
            fileService.unfreezeFile(abuseReportDetails.getFileId());

        }

        return ResponseEntity.ok(abuseReportDetails);
    }
}
