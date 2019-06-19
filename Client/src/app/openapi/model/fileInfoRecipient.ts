/**
 * EasyShare
 * This is a API definition for the EasyShare service.
 *
 * OpenAPI spec version: 0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
import { FileBasics } from './fileBasics';


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
}

