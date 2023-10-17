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


export interface Monitoring { 
    ID?: string;
    datetime?: string;
    event?: Monitoring.EventEnum;
    fileId?: string;
    userId?: string;
    counter?: number;
    status?: Monitoring.StatusEnum;
}
export namespace Monitoring {
    export type EventEnum = 'DOWNLOAD_RATE_HOUR' | 'UPLOAD_RATE_HOUR' | 'DOWNLOAD_RATE_DAY' | 'UPLOAD_RATE_DAY' | 'USER_CREATION_DAY';
    export const EventEnum = {
        DownloadRateHour: 'DOWNLOAD_RATE_HOUR' as EventEnum,
        UploadRateHour: 'UPLOAD_RATE_HOUR' as EventEnum,
        DownloadRateDay: 'DOWNLOAD_RATE_DAY' as EventEnum,
        UploadRateDay: 'UPLOAD_RATE_DAY' as EventEnum,
        UserCreationDay: 'USER_CREATION_DAY' as EventEnum
    };
    export type StatusEnum = 'WAITING' | 'DENIED' | 'APPROVED';
    export const StatusEnum = {
        Waiting: 'WAITING' as StatusEnum,
        Denied: 'DENIED' as StatusEnum,
        Approved: 'APPROVED' as StatusEnum
    };
}


