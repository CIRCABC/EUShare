/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.storage.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import eu.europa.circabc.eushare.storage.entity.DBAbuse.Status;

@Converter(autoApply = true)
public class AbuseReportStatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status Status) {
        return (Status == null) ? null : Status.name();
    }

    @Override
    public Status convertToEntityAttribute(String name) {
        return (name == null) ? null : Status.valueOf(name);
    }
}