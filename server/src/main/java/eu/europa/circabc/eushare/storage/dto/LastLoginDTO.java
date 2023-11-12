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

import eu.europa.circabc.eushare.model.LastLogin;

public class LastLoginDTO {
    private String id;
    private String email;
    private String name;
    private String username;
    private Long total_space;
    private LocalDateTime last_logged;
    private LocalDateTime creation_date;
    private Integer uploads;
    private String status;

    public LastLoginDTO(String id, String email, String name, String username, Long total_space,
            LocalDateTime last_logged, LocalDateTime creation_date, Integer uploads, String status) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.username = username;
        this.total_space = total_space;
        this.last_logged = last_logged;
        this.creation_date = creation_date;
        this.uploads = uploads;
        this.status = status;
    }

    public LastLogin toLastLogin() {
        LastLogin lastLogin = new LastLogin();
        lastLogin.setId(this.id);
        lastLogin.setEmail(this.email);
        lastLogin.setName(this.name);
        lastLogin.setUsername(this.username);
        lastLogin.setTotalSpace(this.total_space);
        if (this.last_logged != null) {
            lastLogin.setLastLogged(this.last_logged.atOffset(ZoneOffset.UTC));
        }
        if (this.creation_date != null) {
            lastLogin.setCreationDate(this.creation_date.atOffset(ZoneOffset.UTC));
        }
        lastLogin.setUploads(this.uploads);
        lastLogin.setStatus(this.status);
        return lastLogin;
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
    public LocalDateTime getCreationDate() {
        return creation_date;
    }
    public void setCreationDate(LocalDateTime creation_date) {
        this.creation_date = creation_date;
    }
    public Integer getUploads() {
        return uploads;
    }
    public void setUploads(Integer uploads) {
        this.uploads = uploads;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
   
}
