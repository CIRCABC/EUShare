/*
 * EUShare - a module of CIRCABC
 * Copyright (C) 2019-2021 European Commission
 *
 * This file is part of the "EUShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */
package eu.europa.circabc.eushare.model;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnumConverter {
    private static final Map<Enum<?>, Enum<?>> cache = new ConcurrentHashMap<>();

    public static <T extends Enum<T>> T convert(Enum<?> enumValue, Class<T> targetEnumClass) {
        // Attempt to get the converted value from the cache
        Enum<?> cachedValue = cache.get(enumValue);
        if (cachedValue != null) {
            return (T) cachedValue;
        }

        try {
            Field field = targetEnumClass.getDeclaredField(enumValue.name());
            @SuppressWarnings("unchecked")
            T convertedValue = (T) field.get(null);

            // Cache the converted value for future use
            cache.put(enumValue, convertedValue);

            return convertedValue;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Unknown role: " + enumValue);
        }
    }
}

 