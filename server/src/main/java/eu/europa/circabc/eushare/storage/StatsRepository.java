/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StatsRepository extends CrudRepository<DBStat, String> {


  @Query(
    value = 
    "SELECT "+
    " (SELECT 0 ) AS stats_id, "+
    " (SELECT :year ) AS year, "+
    " (SELECT :month ) AS month, "+
    " (SELECT COUNT(users.id) FROM users WHERE MONTH(last_logged) =  :month AND YEAR(last_logged) = :year ) AS users,"+
    " (SELECT COUNT(logs.log_id) FROM logs WHERE MONTH(download_date) =  :month AND YEAR(download_date) = :year ) AS downloads ,"+
    " (SELECT COUNT(files.file_id) FROM files WHERE MONTH(created) =  :month AND YEAR(created) = :year ) AS uploads ,"+
    " (SELECT IFNULL(sum(files.file_size),0) FROM files INNER JOIN logs ON files.file_id=logs.file_file_id WHERE MONTH(logs.download_date) =  :month AND YEAR(logs.download_date) = :year ) AS downloads_data ,"+
    " (SELECT IFNULL(sum(files.file_size),0) FROM files WHERE MONTH(files.created) =  :month AND YEAR(files.created) = :year ) AS uploads_data "+
    " FROM DUAL",
    nativeQuery = true
  )
  public DBStat findCurrentStats(
    @Param("month") Integer month,
    @Param("year") Integer year
  );

  public List<DBStat> findByYearOrderByMonthAsc(Integer year);

  public DBStat findByYearAndMonth(Integer year,Integer month);


  // TODO dynamic populate dropdown years // public List<Integer> findDistinctYear();

  // Populate data INSERT INTO  stats (stats_id,year,month,users,downloads,uploads,downloads_data, uploads_data) values (UUID(),2022,12, (FLOOR( 1 + RAND( ) *100 )), (FLOOR( 1 + RAND( ) *300 )), (FLOOR( 1 + RAND( ) *200 )), (FLOOR( 1 + RAND( ) *300*500 )), (FLOOR( 1 + RAND( ) *200*500 )));

// SELECT 
// (SELECT 0 ) AS stats_id, 
// (SELECT 2023 ) AS year, 
// (SELECT 1 ) AS month, 
// (SELECT COUNT(users.id) FROM users WHERE MONTH(last_logged) =  1 AND YEAR(last_logged) = 2023 ) AS users,
// (SELECT COUNT(logs.log_id) FROM logs WHERE MONTH(download_date) =  1 AND YEAR(download_date) = 2023 ) AS downloads ,
// (SELECT COUNT(files.file_id) FROM files WHERE MONTH(created) =  1 AND YEAR(created) = 2023 ) AS uploads ,
// (SELECT IFNULL(sum(files.file_size),0) FROM files INNER JOIN logs ON files.file_id=logs.file_file_id WHERE MONTH(logs.download_date) =  1 AND YEAR(logs.download_date) = 2023 ) AS downloads_data ,
// (SELECT IFNULL(sum(files.file_size),0) FROM files WHERE MONTH(files.created) =  1 AND YEAR(files.created) = 2023 ) AS uploads_data 
// FROM DUAL


}
