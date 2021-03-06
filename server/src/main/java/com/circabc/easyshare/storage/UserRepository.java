/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.storage;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends PagingAndSortingRepository<DBUser, String> {
    public List<DBUser> findAllById(String id);
    public DBUser findOneByUsername(String username);
    public DBUser findOneByEmailIgnoreCase(String email);

    @Query("FROM DBUser u WHERE (u.email like upper(concat(:start,'%')) or (u.name like upper(concat('%',:start,'%')))) and (u.role='INTERNAL' or u.role='ADMIN')")
    public List<DBUser> findByEmailRoleInternalOrAdmin(@Param("start") String start, Pageable page);
    
    public DBUser findOneByEmailIgnoreCaseAndRole(String email, DBUser.Role role);
    public DBUser findOneByName(String name);
    public DBUser findOneByNameAndRole(String name, DBUser.Role role);
    public List<DBUser> findByEmailIgnoreCaseStartsWith(String start, Pageable page);
}

