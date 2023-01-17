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


export interface Stat { 
    /**
     * ID of the statistic record
     */
    statId: string;
    /**
     * year
     */
    year: number;
    /**
     * month
     */
    month: number;
    /**
     * users
     */
    users: number;
    /**
     * number of downloads
     */
    downloads: number;
    /**
     * number of uploads
     */
    uploads: number;
    /**
     * total size of downloads
     */
    downloadsData: number;
    /**
     * total size of uploads
     */
    uploadsData: number;
}
