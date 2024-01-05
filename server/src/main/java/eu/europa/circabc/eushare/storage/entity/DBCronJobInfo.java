/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cronjob_info")
public class DBCronJobInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

 
    @Version
    @Column(name = "version")
    private Long version;


    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }


    @Column(name = "cronjob_name", unique = true)
    private String cronjobName;

    @Column(name = "cronjob_delay")
    private String cronjobDelay;

    @Column(name = "is_locked")
    private Boolean isLocked;

    @Column(name = "last_run_datetime")
    private LocalDateTime lastRunDateTime;

    @Column(name = "master_server_id")
    private String masterServerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCronjobName() {
        return cronjobName;
    }

    public void setCronjobName(String cronjobName) {
        this.cronjobName = cronjobName;
    }

    public String getCronjobDelay() {
        return cronjobDelay;
    }

    public void setCronjobDelay(String cronjobDelay) {
        this.cronjobDelay = cronjobDelay;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public LocalDateTime getLastRunDateTime() {
        return lastRunDateTime;
    }

    public void setLastRunDateTime(LocalDateTime lastRunDateTime) {
        this.lastRunDateTime = lastRunDateTime;
    }

    public String getMasterServerId() {
        return masterServerId;
    }

    public void setMasterServerId(String masterServerId) {
        this.masterServerId = masterServerId;
    }


}
