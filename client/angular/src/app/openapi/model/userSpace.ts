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
