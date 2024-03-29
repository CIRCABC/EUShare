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


export interface AbuseReportDetails { 
    ID?: string;
    reporter?: string;
    fileId?: string;
    reason?: string;
    description?: string;
    date?: string;
    status?: AbuseReportDetails.StatusEnum;
    filename?: string;
    filesize?: number;
    shortUrl?: string;
    uploader_email?: string;
    uploader_name?: string;
    uploader_status?: string;
}
export namespace AbuseReportDetails {
    export type StatusEnum = 'WAITING' | 'DENIED' | 'APPROVED';
    export const StatusEnum = {
        Waiting: 'WAITING' as StatusEnum,
        Denied: 'DENIED' as StatusEnum,
        Approved: 'APPROVED' as StatusEnum
    };
}


