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


export interface UserSpace { 
    /**
     * Total space the user has (Bytes)
     */
    totalSpace: number;
    /**
     * Space the user already used (Bytes)
     */
    usedSpace: number;
}

