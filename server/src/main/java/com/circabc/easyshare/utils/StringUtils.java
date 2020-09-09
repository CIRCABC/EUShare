/**
 * EasyShare - a module of CIRCABC
 * Copyright (C) 2019 European Commission
 *
 * This file is part of the "EasyShare" project.
 *
 * This code is publicly distributed under the terms of EUPL-V1.2 license,
 * available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
 */

package com.circabc.easyshare.utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    // only allow lowercase as NTFS does not differentiate between cases
    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789-_";
    private static final SecureRandom random = new SecureRandom();

    private StringUtils() {
    }

    /**
     * Decodes a base64-encoded String.
     */
    public static String decodeBase64(String encodedString) {
        return new String(Base64.getDecoder().decode(encodedString));
    }

    /**
     * Generates a cryptographically secure string with length 20.
     */
    public static String randomString() {
        return randomString(20);
    }

    /**
     * Generates a cryptographically secure string with specified length.
     *
     * @throws IllegalArgumentException if {@code length <= 0}.
     */
    public static String randomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException();
        }

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALLOWED_CHARACTERS.length());
            sb.append(ALLOWED_CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern
            .compile("^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,4}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmailAddress(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static final Pattern VALID_USERNAME_REGEX = Pattern.compile("^[A-Za-z0-9]{2,25}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateLinkName(String username) {
        Matcher matcher = VALID_USERNAME_REGEX.matcher(username);
        return matcher.find();
    }

    public static boolean validateMessage(String message) {
        return (message == null || (message != null && message.length() < 401));
    }

    public static String emailToGivenName(String email) {
        if (email != null && email.indexOf("@") != -1) {
            String givenName = email.substring(0, email.indexOf("@"));
            givenName = givenName.replaceAll("\\.", " ");
            return givenName;
        }
        return null;
    }

}
