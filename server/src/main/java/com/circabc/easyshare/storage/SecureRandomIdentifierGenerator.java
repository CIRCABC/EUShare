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

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.circabc.easyshare.utils.StringUtils;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class SecureRandomIdentifierGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        String query = String.format("select %s from %s", 
            session.getEntityPersister(object.getClass().getName(), object)
              .getIdentifierPropertyName(),
              object.getClass().getSimpleName());

        String possibleId = StringUtils.randomString();
        List<String> ids = (List<String>) session.createQuery(query).stream().collect(Collectors.toList());
        while(ids.contains(possibleId)) {
            possibleId = StringUtils.randomString();
        }
        return possibleId;
    }
}