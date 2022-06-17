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

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends PagingAndSortingRepository<DBUser, String> {
    public List<DBUser> findAllById(String id);
    public DBUser findOneByUsername(String username);

    @Query("FROM DBUser u WHERE (u.email = (:email))")
    public DBUser findOneByEmailIgnoreCase(@Param("email") String email);

    @Query("FROM DBUser u WHERE (u.email like lower(concat(:start,'%')) or (u.name like lowera(concat('%',:start,'%')))) and (u.role='INTERNAL' or u.role='ADMIN') ORDER BY u.name")
    public List<DBUser> findByEmailRoleInternalOrAdmin(@Param("start") String start, Pageable page);
    
    public DBUser findOneByEmailIgnoreCaseAndRole(String email, DBUser.Role role);
    public DBUser findOneByName(String name);
    public DBUser findOneById(String id);
    public DBUser findOneByNameAndRole(String name, DBUser.Role role);
    public List<DBUser> findByEmailIgnoreCaseStartsWith(String start, Pageable page);
}

