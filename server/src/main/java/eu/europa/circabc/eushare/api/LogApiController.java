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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
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

@RestController
public class LogApiController implements LogApi {

    @Autowired
    private FileLogsRepository fileLogsRepository;

    @AdminOnly
    @Override
    public ResponseEntity<Resource> logGetAllLastLogsGet() {
        List<LastLogDTO> logs = fileLogsRepository.getAllLastLogs();
        String csvContent = toCSV(logs);

        InputStream stream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        InputStreamResource inputStreamResource = new InputStreamResource(stream);

        HttpHeaders responseHeaders = new HttpHeaders();
        ContentDisposition cd = ContentDisposition
                .builder("attachment")
                .filename("last_logs.csv", StandardCharsets.UTF_8)
                .build();
        responseHeaders.setContentDisposition(cd);
        responseHeaders.setContentLength(csvContent.length());
        responseHeaders.setContentType(MediaType.parseMediaType("text/csv"));

        ResponseEntity<Resource> responseEntity = new ResponseEntity<>(
                inputStreamResource,
                responseHeaders,
                HttpStatus.OK);

        return responseEntity;
    }

    @AdminOnly
    @Override
    public ResponseEntity<Resource> logGetAllLastDownloadsGet() {
        List<LastDownloadDTO> downloads = fileLogsRepository.getAllLastDownloads();
        String csvContent = toCSV(downloads);

        InputStream stream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        InputStreamResource inputStreamResource = new InputStreamResource(stream);

        HttpHeaders responseHeaders = new HttpHeaders();
        ContentDisposition cd = ContentDisposition
                .builder("attachment")
                .filename("last_downloads.csv", StandardCharsets.UTF_8)
                .build();
        responseHeaders.setContentDisposition(cd);
        responseHeaders.setContentLength(csvContent.length());
        responseHeaders.setContentType(MediaType.parseMediaType("text/csv"));

        ResponseEntity<Resource> responseEntity = new ResponseEntity<>(
                inputStreamResource,
                responseHeaders,
                HttpStatus.OK);

        return responseEntity;
    }

    @AdminOnly
    @Override
    public ResponseEntity<Resource> logGetAllLastUploadsGet() {
        List<LastUploadDTO> uploads = fileLogsRepository.getAllLastUploads();
        String csvContent = toCSV(uploads);

        InputStream stream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        InputStreamResource inputStreamResource = new InputStreamResource(stream);

        HttpHeaders responseHeaders = new HttpHeaders();
        ContentDisposition cd = ContentDisposition
                .builder("attachment")
                .filename("last_uploads.csv", StandardCharsets.UTF_8)
                .build();
        responseHeaders.setContentDisposition(cd);
        responseHeaders.setContentLength(csvContent.length());
        responseHeaders.setContentType(MediaType.parseMediaType("text/csv"));

        ResponseEntity<Resource> responseEntity = new ResponseEntity<>(
                inputStreamResource,
                responseHeaders,
                HttpStatus.OK);

        return responseEntity;
    }


    private Pageable createPageRequest(Integer pageSize, Integer pageNumber) {
        return PageRequest.of(pageNumber, pageSize);
    }

    @AdminOnly
    @Override
    public ResponseEntity<List<LastDownload>> logGetLastDownloadsGet(
            @Valid @RequestParam(value = "pageSize", required = true) Integer pageSize,
            @Valid @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
            @Valid @RequestParam(value = "sortField", required = false) String sortField,
            @Valid @RequestParam(value = "sortOrder", required = false) String sortOrder) {

        List<LastDownloadDTO> lastDownloadDTOs = fileLogsRepository
                .getLastDownloads(createPageRequest(pageSize, pageNumber), sortField, sortOrder);

        List<LastDownload> lastDownloads = new ArrayList<>();
        for (LastDownloadDTO dto : lastDownloadDTOs) {
            lastDownloads.add(dto.toLastDownload());
        }
        return new ResponseEntity<>(lastDownloads, HttpStatus.OK);
    }

    @AdminOnly
    @Override
    public ResponseEntity<List<LastLog>> logGetLastLogsGet(
            @Valid @RequestParam(value = "pageSize", required = true) Integer pageSize,
            @Valid @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
            @Valid @RequestParam(value = "sortField", required = false) String sortField,
            @Valid @RequestParam(value = "sortOrder", required = false) String sortOrder) {

        List<LastLogDTO> lastLogDTOs = fileLogsRepository
                .getLastLogs(createPageRequest(pageSize, pageNumber), sortField, sortOrder);

        List<LastLog> lastLogs = new ArrayList<>();
        for (LastLogDTO dto : lastLogDTOs) {
            lastLogs.add(dto.toLastLog());
        }
        return new ResponseEntity<>(lastLogs, HttpStatus.OK);
    }

    @AdminOnly
    @Override
    public ResponseEntity<List<LastUpload>> logGetLastUploadsGet(
            @Valid @RequestParam(value = "pageSize", required = true) Integer pageSize,
            @Valid @RequestParam(value = "pageNumber", required = true) Integer pageNumber,
            @Valid @RequestParam(value = "sortField", required = false) String sortField,
            @Valid @RequestParam(value = "sortOrder", required = false) String sortOrder) {

        List<LastUploadDTO> lastUploadDTOs = fileLogsRepository
                .getLastUploads(createPageRequest(pageSize, pageNumber), sortField, sortOrder);

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

    private String toCSV(List<?> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        StringJoiner csvHeader = new StringJoiner(",");
        StringJoiner csvBody = new StringJoiner("\n");

        Field[] fields = list.get(0).getClass().getDeclaredFields();

        for (Field field : fields) {
            csvHeader.add(field.getName());
        }

        for (Object obj : list) {
            StringJoiner row = new StringJoiner(",");
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    row.add(value != null ? value.toString() : "");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            csvBody.add(row.toString());
        }

        return csvHeader.toString() + "\n" + csvBody.toString();
    }

}
