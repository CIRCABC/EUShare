/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
/**
 * CIRCABC Share
 * This is a API definition for the CIRCABC Share service.
 *
 * The version of the OpenAPI document: 0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


export interface FileInfoRecipient { 
    /**
     * Expiration date of file
     */
    expirationDate: string;
    /**
     * File is password-protected
     */
    hasPassword: boolean;
    /**
     * Filename
     */
    name: string;
    /**
     * Size of file (Bytes)
     */
    size: number;
    /**
     * name of the uploader
     */
    uploaderName: string;
    /**
     * download file id
     */
    fileId: string;
}

