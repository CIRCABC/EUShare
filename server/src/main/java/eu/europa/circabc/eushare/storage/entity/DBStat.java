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

import eu.europa.circabc.eushare.model.Stat;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(
  name = "Stats",
  indexes = {
    @Index(
      name = "INDEX_YEAR",
      columnList = "year",
      unique = false
    ),
    @Index(
      name = "INDEX_MONTH",
      columnList = "month",
      unique = false
    ),
  }
)
public class DBStat {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "STATS_ID", nullable = false)
  private String id;


  @Column(name = "year", nullable = false)
  private int year;

  @Column(name = "month", nullable = false)
  private int month;

  @Column(name = "users", nullable = false)
  private long users;

  @Column(name = "downloads", nullable = false)
  private long downloads;

  @Column(name = "uploads", nullable = false)
  private long uploads;

  @Column(name = "downloadsData", nullable = false)
  private long downloadsData;

  @Column(name = "uploadsData", nullable = false)
  private long uploadsData;


  

  public DBStat( int year, int month, long users, long downloads, long uploads, long downloadsData,
      long uploadsData) {

    this.year = year;
    this.month = month;
    this.users = users;
    this.downloads = downloads;
    this.uploads = uploads;
    this.downloadsData = downloadsData;
    this.uploadsData = uploadsData;
  }

  

  private DBStat() {}

  public Stat toStat(){
    Stat stat = new Stat();
    stat.setYear(new BigDecimal(this.year));
    stat.setMonth(new BigDecimal(this.month));
    stat.setUsers(new BigDecimal(this.users));
    stat.setDownloads(new BigDecimal(this.downloads));
    stat.setUploads(new BigDecimal(this.uploads));
    stat.setDownloadsData(new BigDecimal(this.downloadsData));
    stat.setUploadsData(new BigDecimal(this.uploadsData));
    return stat;
  }

  public int getYear() {
    return year;
  }



  public void setYear(int year) {
    this.year = year;
  }



  public int getMonth() {
    return month;
  }



  public void setMonth(int month) {
    this.month = month;
  }



  public long getUsers() {
    return users;
  }



  public void setUsers(long users) {
    this.users = users;
  }



  public long getDownloads() {
    return downloads;
  }



  public void setDownloads(long downloads) {
    this.downloads = downloads;
  }



  public long getUploads() {
    return uploads;
  }



  public void setUploads(long uploads) {
    this.uploads = uploads;
  }



  public long getDownloadsData() {
    return downloadsData;
  }



  public void setDownloadsData(long downloadsData) {
    this.downloadsData = downloadsData;
  }



  public long getUploadsData() {
    return uploadsData;
  }



  public void setUploadsData(long uploadsData) {
    this.uploadsData = uploadsData;
  }



  @Override
  public String toString() {
    return "DBStats [id=" + id + ", year=" + year + ", month=" + month + ", users=" + users + ", downloads=" + downloads
        + ", uploads=" + uploads + ", downloadsData=" + downloadsData + ", uploadsData=" + uploadsData + "]";
  }

  

}
