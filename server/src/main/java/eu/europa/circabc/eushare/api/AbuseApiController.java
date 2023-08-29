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

import eu.europa.circabc.eushare.error.HttpErrorAnswerBuilder;
import eu.europa.circabc.eushare.exceptions.WrongAuthenticationException;
import eu.europa.circabc.eushare.model.AbuseReport;
import eu.europa.circabc.eushare.model.AbuseReportDetails;
import eu.europa.circabc.eushare.model.InlineObject1;
import eu.europa.circabc.eushare.model.TrustRequest;
import eu.europa.circabc.eushare.services.FileService;
import eu.europa.circabc.eushare.services.UserService;
import eu.europa.circabc.eushare.storage.dto.AbuseReportDetailsDTO;
import eu.europa.circabc.eushare.storage.entity.DBAbuse;
import eu.europa.circabc.eushare.storage.entity.DBShare;
import eu.europa.circabc.eushare.storage.entity.DBTrust;
import eu.europa.circabc.eushare.storage.entity.DBUser;
import eu.europa.circabc.eushare.storage.repository.AbuseRepository;
import eu.europa.circabc.eushare.storage.repository.TrustRepository;
import eu.europa.circabc.eushare.storage.repository.UserRepository;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
public class AbuseApiController implements AbuseApi {

    @Autowired
    private AbuseRepository abuseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserService userService;

    @Autowired
    public FileService fileService;

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

        if (dbAbuse.getFiledId().length() == DBShare.SHORT_URL_LENGTH) {
            DBShare share = fileService.findShare(dbAbuse.getFiledId());
            String file_id = share.getFile().getId();
            dbAbuse.setFiledId(file_id);
        }

        dbAbuse.setDate(LocalDate.now());
        dbAbuse.setStatus(false);

        // abuseRepository.save(dbAbuse);

        return ResponseEntity.ok(abuseReport);

    }

    @Override
    public ResponseEntity<Void> deleteAbuseReport(@PathVariable("id") String id) {
        DBAbuse dbAbuse = abuseRepository.findOneById(id);
        if (dbAbuse == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Abuse report not found");
        }
        // abuseRepository.delete(dbAbuse);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<AbuseReportDetails>> getAbuseReportList() {

        List<AbuseReportDetailsDTO> abuseReportDetailsDto = abuseRepository.findAllDetails();
        List<AbuseReportDetails> abuseReportDetails = new ArrayList<>();

        for (AbuseReportDetailsDTO dto : abuseReportDetailsDto) {
            AbuseReportDetails detail = dto.toAbuseReportDetails();
            abuseReportDetails.add(detail);
        }

        return ResponseEntity.ok(abuseReportDetails);
    }

    @Override
    public ResponseEntity<AbuseReport> updateAbuseReport(@PathVariable("id") String id,
            @Valid @RequestBody AbuseReport abuseReport) {
        DBAbuse dbAbuse = abuseRepository.findOneById(abuseReport.getID());
        if (dbAbuse == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Abuse report not found");
        }

        dbAbuse.setStatus(abuseReport.getStatus());

        // dbAbuse = abuseRepository.save(dbAbuse);
        return ResponseEntity.ok(dbAbuse.toAbuseReport());
    }
}
