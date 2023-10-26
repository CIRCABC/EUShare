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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import eu.europa.circabc.eushare.model.LastDownload;
import eu.europa.circabc.eushare.model.LastLog;
import eu.europa.circabc.eushare.model.LastUpload;
import eu.europa.circabc.eushare.model.Metadata;
import eu.europa.circabc.eushare.security.AdminOnly;
import eu.europa.circabc.eushare.storage.dto.LastDownloadDTO;
import eu.europa.circabc.eushare.storage.dto.LastLogDTO;
import eu.europa.circabc.eushare.storage.dto.LastUploadDTO;
import eu.europa.circabc.eushare.storage.repository.FileLogsRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
public class LogApiController implements LogApi {

    @Autowired
    private FileLogsRepository fileLogsRepository;

    private Pageable createPageRequest(Integer pageSize, Integer pageNumber) {
        return PageRequest.of(pageNumber, pageSize);
    }

    @AdminOnly
    @Override
    public ResponseEntity<List<LastDownload>> logGetLastDownloadsGet(Integer pageSize, Integer pageNumber) {
        List<LastDownloadDTO> lastDownloadDTOs = fileLogsRepository
                .getLastDownloads(createPageRequest(pageSize, pageNumber));
        List<LastDownload> lastDownloads = new ArrayList<>();

        for (LastDownloadDTO dto : lastDownloadDTOs) {
            lastDownloads.add(dto.toLastDownload());
        }

        return new ResponseEntity<>(lastDownloads, HttpStatus.OK);
    }

    @AdminOnly
    @Override
    public ResponseEntity<List<LastLog>> logGetLastLogsGet(Integer pageSize, Integer pageNumber) {
        List<LastLogDTO> lastLogDTOs = fileLogsRepository.getLastLogs(createPageRequest(pageSize, pageNumber));
        List<LastLog> lastLogs = new ArrayList<>();

        for (LastLogDTO dto : lastLogDTOs) {
            lastLogs.add(dto.toLastLog());
        }

        return new ResponseEntity<>(lastLogs, HttpStatus.OK);
    }

    @AdminOnly
    @Override
    public ResponseEntity<List<LastUpload>> logGetLastUploadsGet(Integer pageSize, Integer pageNumber) {
        List<LastUploadDTO> lastUploadDTOs = fileLogsRepository.getLastUploads(createPageRequest(pageSize, pageNumber));
        List<LastUpload> lastUploads = new ArrayList<>();

        for (LastUploadDTO dto : lastUploadDTOs) {
            lastUploads.add(dto.toLastUpload());
        }

        return new ResponseEntity<>(lastUploads, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Metadata> logGetLastDownloadsMetadataGet() {
        long total = fileLogsRepository.countLastDownloads();
        Metadata metadata = new Metadata();
        metadata.setTotal(total);
        return ResponseEntity.ok(metadata);
    }

    @Override
    public ResponseEntity<Metadata> logGetLastLogsMetadataGet() {
        long total = fileLogsRepository.countLastLogs();
        Metadata metadata = new Metadata();
        metadata.setTotal(total);
        return ResponseEntity.ok(metadata);
    }

    @Override
    public ResponseEntity<Metadata> logGetLastUploadsMetadataGet() {
        long total = fileLogsRepository.countLastUploads();
        Metadata metadata = new Metadata();
        metadata.setTotal(total);
        return ResponseEntity.ok(metadata);
    }

}
