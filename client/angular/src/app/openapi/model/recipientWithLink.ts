/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
/**
 * EasyShare
 * This is a API definition for the EasyShare service.
 *
 * The version of the OpenAPI document: 0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { Recipient } from './recipient';


export interface RecipientWithLink { 
    /**
     * Email or name of the recipient
     */
    emailOrName: string;
    /**
     * Optional message to send
     */
    message?: string;
    /**
     * True to send an email with the download link
     */
    sendEmail: boolean;
    /**
     * Id of the recipient
     */
    recipientId: string;
    /**
     * Download link to a specific file
     */
    downloadLink: string;
}

