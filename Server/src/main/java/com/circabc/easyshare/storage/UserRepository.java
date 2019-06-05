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
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends PagingAndSortingRepository<DBUser, String> {
    public List<DBUser> findAllById(String id);
    public DBUser findOneByUsername(String username);
    public DBUser findOneByEmail(String email);
    public DBUser findOneByName(String name);
    public List<DBUser> findByEmailStartsWith(String start, Pageable page);
}
