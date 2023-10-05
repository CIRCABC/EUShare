/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage.dto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import eu.europa.circabc.eushare.model.LastLog;

public class LastLogDTO {
    private String id;
    private String email;
    private String name;
    private String username;
    private Long total_space;
    private LocalDateTime last_logged;
    private String status;

    
    public LastLogDTO(String id, String email, String name, String username, Long total_space,
            LocalDateTime last_logged, String status) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.username = username;
        this.total_space = total_space;
        this.last_logged = last_logged;
        this.status = status;
    }

    public LastLog toLastLog() {
        LastLog lastLog = new LastLog();
        lastLog.setId(this.id);
        lastLog.setEmail(this.email);
        lastLog.setName(this.name);
        lastLog.setUsername(this.username);
        lastLog.setTotalSpace(this.total_space); 
        if (this.last_logged != null) {
            lastLog.setLastLogged(this.last_logged.atOffset(ZoneOffset.UTC)); // convert from LocalDateTime to OffsetDateTime with UTC offset
        }
        lastLog.setStatus(this.status);
        return lastLog;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Long getTotal_space() {
        return total_space;
    }
    public void setTotal_space(Long total_space) {
        this.total_space = total_space;
    }
    public LocalDateTime getLast_logged() {
        return last_logged;
    }
    public void setLast_logged(LocalDateTime last_logged) {
        this.last_logged = last_logged;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
   
}
