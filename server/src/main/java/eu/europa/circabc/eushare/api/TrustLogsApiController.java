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

import eu.europa.circabc.eushare.model.TrustLog;
import eu.europa.circabc.eushare.storage.entity.DBTrustLog;
import eu.europa.circabc.eushare.storage.repository.TrustLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TrustLogsApiController implements TrustLogsApi {
    
    @Autowired
    private TrustLogRepository trustLogRepository;

    public TrustLogsApiController(TrustLogRepository trustLogRepository) {
        this.trustLogRepository = trustLogRepository;
    }

    @Override
    public ResponseEntity<List<TrustLog>> getAllTrustLogs() {
        List<TrustLog> trustLogs = new ArrayList<>();
        for (DBTrustLog dbTrustLog : trustLogRepository.findAll()) {
            trustLogs.add(dbTrustLog.toTrustLog());
        }
        return ResponseEntity.ok(trustLogs);
    }
}
