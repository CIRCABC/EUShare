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
/* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional }                      from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams,
         HttpResponse, HttpEvent, HttpParameterCodec
        }       from '@angular/common/http';
import { CustomHttpParameterCodec }                          from '../encoder';
import { Observable }                                        from 'rxjs';

// @ts-ignore
import { FileInfoRecipient } from '../model/fileInfoRecipient';
// @ts-ignore
import { FileInfoUploader } from '../model/fileInfoUploader';
// @ts-ignore
import { Status } from '../model/status';
// @ts-ignore
import { UserInfo } from '../model/userInfo';

// @ts-ignore
import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';



@Injectable({
  providedIn: 'root'
})
export class UsersService {

    protected basePath = 'http://localhost:8080';
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();
    public encoder: HttpParameterCodec;

    constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string|string[], @Optional() configuration: Configuration) {
        if (configuration) {
            this.configuration = configuration;
        }
        if (typeof this.configuration.basePath !== 'string') {
            if (Array.isArray(basePath) && basePath.length > 0) {
                basePath = basePath[0];
            }

            if (typeof basePath !== 'string') {
                basePath = this.basePath;
            }
            this.configuration.basePath = basePath;
        }
        this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
    }


    // @ts-ignore
    private addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
        if (typeof value === "object" && value instanceof Date === false) {
            httpParams = this.addToHttpParamsRecursive(httpParams, value);
        } else {
            httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
        }
        return httpParams;
    }

    private addToHttpParamsRecursive(httpParams: HttpParams, value?: any, key?: string): HttpParams {
        if (value == null) {
            return httpParams;
        }

        if (typeof value === "object") {
            if (Array.isArray(value)) {
                (value as any[]).forEach( elem => httpParams = this.addToHttpParamsRecursive(httpParams, elem, key));
            } else if (value instanceof Date) {
                if (key != null) {
                    httpParams = httpParams.append(key, (value as Date).toISOString().substring(0, 10));
                } else {
                   throw Error("key may not be null if value is Date");
                }
            } else {
                Object.keys(value).forEach( k => httpParams = this.addToHttpParamsRecursive(
                    httpParams, value[k], key != null ? `${key}.${k}` : k));
            }
        } else if (key != null) {
            httpParams = httpParams.append(key, value);
        } else {
            throw Error("key may not be null if value is not object or array");
        }
        return httpParams;
    }

    /**
     * Used by the INTERNAL users in order to search the files they have recieved
     * @param userID The id of the user
     * @param pageSize Number of files returned
     * @param pageNumber Page number
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getFilesFileInfoRecipient(userID: string, pageSize: number, pageNumber: number, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<Array<FileInfoRecipient>>;
    public getFilesFileInfoRecipient(userID: string, pageSize: number, pageNumber: number, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<HttpResponse<Array<FileInfoRecipient>>>;
    public getFilesFileInfoRecipient(userID: string, pageSize: number, pageNumber: number, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<HttpEvent<Array<FileInfoRecipient>>>;
    public getFilesFileInfoRecipient(userID: string, pageSize: number, pageNumber: number, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json',}): Observable<any> {
        if (userID === null || userID === undefined) {
            throw new Error('Required parameter userID was null or undefined when calling getFilesFileInfoRecipient.');
        }
        if (pageSize === null || pageSize === undefined) {
            throw new Error('Required parameter pageSize was null or undefined when calling getFilesFileInfoRecipient.');
        }
        if (pageNumber === null || pageNumber === undefined) {
            throw new Error('Required parameter pageNumber was null or undefined when calling getFilesFileInfoRecipient.');
        }

        let localVarQueryParameters = new HttpParams({encoder: this.encoder});
        if (pageSize !== undefined && pageSize !== null) {
          localVarQueryParameters = this.addToHttpParams(localVarQueryParameters,
            <any>pageSize, 'pageSize');
        }
        if (pageNumber !== undefined && pageNumber !== null) {
          localVarQueryParameters = this.addToHttpParams(localVarQueryParameters,
            <any>pageNumber, 'pageNumber');
        }

        let localVarHeaders = this.defaultHeaders;

        let localVarCredential: string | undefined;
        // authentication (OpenID) required
        localVarCredential = this.configuration.lookupCredential('OpenID');
        if (localVarCredential) {
        }

        let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (localVarHttpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (localVarHttpHeaderAcceptSelected !== undefined) {
            localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
        }



        let responseType_: 'text' | 'json' | 'blob' = 'json';
        if (localVarHttpHeaderAcceptSelected) {
            if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
                responseType_ = 'text';
            } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
                responseType_ = 'json';
            } else {
                responseType_ = 'blob';
            }
        }

        let localVarPath = `/user/${this.configuration.encodeParam({name: "userID", value: userID, in: "path", style: "simple", explode: false, dataType: "string", dataFormat: undefined})}/files/fileInfoRecipient`;
        return this.httpClient.request<Array<FileInfoRecipient>>('get', `${this.configuration.basePath}${localVarPath}`,
            {
                params: localVarQueryParameters,
                responseType: <any>responseType_,
                withCredentials: this.configuration.withCredentials,
                headers: localVarHeaders,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Used by the INTERNAL users in order to search their own files\&#39; FileInfoUploader
     * @param userID The id of the user
     * @param pageSize Number of files returned
     * @param pageNumber Page number
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getFilesFileInfoUploader(userID: string, pageSize: number, pageNumber: number, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<Array<FileInfoUploader>>;
    public getFilesFileInfoUploader(userID: string, pageSize: number, pageNumber: number, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<HttpResponse<Array<FileInfoUploader>>>;
    public getFilesFileInfoUploader(userID: string, pageSize: number, pageNumber: number, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<HttpEvent<Array<FileInfoUploader>>>;
    public getFilesFileInfoUploader(userID: string, pageSize: number, pageNumber: number, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json',}): Observable<any> {
        if (userID === null || userID === undefined) {
            throw new Error('Required parameter userID was null or undefined when calling getFilesFileInfoUploader.');
        }
        if (pageSize === null || pageSize === undefined) {
            throw new Error('Required parameter pageSize was null or undefined when calling getFilesFileInfoUploader.');
        }
        if (pageNumber === null || pageNumber === undefined) {
            throw new Error('Required parameter pageNumber was null or undefined when calling getFilesFileInfoUploader.');
        }

        let localVarQueryParameters = new HttpParams({encoder: this.encoder});
        if (pageSize !== undefined && pageSize !== null) {
          localVarQueryParameters = this.addToHttpParams(localVarQueryParameters,
            <any>pageSize, 'pageSize');
        }
        if (pageNumber !== undefined && pageNumber !== null) {
          localVarQueryParameters = this.addToHttpParams(localVarQueryParameters,
            <any>pageNumber, 'pageNumber');
        }

        let localVarHeaders = this.defaultHeaders;

        let localVarCredential: string | undefined;
        // authentication (OpenID) required
        localVarCredential = this.configuration.lookupCredential('OpenID');
        if (localVarCredential) {
        }

        let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (localVarHttpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (localVarHttpHeaderAcceptSelected !== undefined) {
            localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
        }



        let responseType_: 'text' | 'json' | 'blob' = 'json';
        if (localVarHttpHeaderAcceptSelected) {
            if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
                responseType_ = 'text';
            } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
                responseType_ = 'json';
            } else {
                responseType_ = 'blob';
            }
        }

        let localVarPath = `/user/${this.configuration.encodeParam({name: "userID", value: userID, in: "path", style: "simple", explode: false, dataType: "string", dataFormat: undefined})}/files/fileInfoUploader`;
        return this.httpClient.request<Array<FileInfoUploader>>('get', `${this.configuration.basePath}${localVarPath}`,
            {
                params: localVarQueryParameters,
                responseType: <any>responseType_,
                withCredentials: this.configuration.withCredentials,
                headers: localVarHeaders,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Used by the users in order to fetch their personal information
     * @param userID The id of the user
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getUserUserInfo(userID: string, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<UserInfo>;
    public getUserUserInfo(userID: string, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<HttpResponse<UserInfo>>;
    public getUserUserInfo(userID: string, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<HttpEvent<UserInfo>>;
    public getUserUserInfo(userID: string, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json',}): Observable<any> {
        if (userID === null || userID === undefined) {
            throw new Error('Required parameter userID was null or undefined when calling getUserUserInfo.');
        }

        let localVarHeaders = this.defaultHeaders;

        let localVarCredential: string | undefined;
        // authentication (OpenID) required
        localVarCredential = this.configuration.lookupCredential('OpenID');
        if (localVarCredential) {
        }

        let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (localVarHttpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (localVarHttpHeaderAcceptSelected !== undefined) {
            localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
        }



        let responseType_: 'text' | 'json' | 'blob' = 'json';
        if (localVarHttpHeaderAcceptSelected) {
            if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
                responseType_ = 'text';
            } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
                responseType_ = 'json';
            } else {
                responseType_ = 'blob';
            }
        }

        let localVarPath = `/user/${this.configuration.encodeParam({name: "userID", value: userID, in: "path", style: "simple", explode: false, dataType: "string", dataFormat: undefined})}/userInfo`;
        return this.httpClient.request<UserInfo>('get', `${this.configuration.basePath}${localVarPath}`,
            {
                responseType: <any>responseType_,
                withCredentials: this.configuration.withCredentials,
                headers: localVarHeaders,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Used by the administrators in order to search for INTERNAL users\&#39; UserInfo
     * @param pageSize Number of persons returned
     * @param pageNumber Page number
     * @param searchString 
     * @param active 
     * @param sortBy Sort by criteria
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getUsersUserInfo(pageSize: number, pageNumber: number, searchString: string, active: boolean, sortBy?: string, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<Array<UserInfo>>;
    public getUsersUserInfo(pageSize: number, pageNumber: number, searchString: string, active: boolean, sortBy?: string, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<HttpResponse<Array<UserInfo>>>;
    public getUsersUserInfo(pageSize: number, pageNumber: number, searchString: string, active: boolean, sortBy?: string, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<HttpEvent<Array<UserInfo>>>;
    public getUsersUserInfo(pageSize: number, pageNumber: number, searchString: string, active: boolean, sortBy?: string, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json',}): Observable<any> {
        if (pageSize === null || pageSize === undefined) {
            throw new Error('Required parameter pageSize was null or undefined when calling getUsersUserInfo.');
        }
        if (pageNumber === null || pageNumber === undefined) {
            throw new Error('Required parameter pageNumber was null or undefined when calling getUsersUserInfo.');
        }
        if (searchString === null || searchString === undefined) {
            throw new Error('Required parameter searchString was null or undefined when calling getUsersUserInfo.');
        }
        if (active === null || active === undefined) {
            throw new Error('Required parameter active was null or undefined when calling getUsersUserInfo.');
        }

        let localVarQueryParameters = new HttpParams({encoder: this.encoder});
        if (pageSize !== undefined && pageSize !== null) {
          localVarQueryParameters = this.addToHttpParams(localVarQueryParameters,
            <any>pageSize, 'pageSize');
        }
        if (pageNumber !== undefined && pageNumber !== null) {
          localVarQueryParameters = this.addToHttpParams(localVarQueryParameters,
            <any>pageNumber, 'pageNumber');
        }
        if (searchString !== undefined && searchString !== null) {
          localVarQueryParameters = this.addToHttpParams(localVarQueryParameters,
            <any>searchString, 'searchString');
        }
        if (sortBy !== undefined && sortBy !== null) {
          localVarQueryParameters = this.addToHttpParams(localVarQueryParameters,
            <any>sortBy, 'sortBy');
        }
        if (active !== undefined && active !== null) {
          localVarQueryParameters = this.addToHttpParams(localVarQueryParameters,
            <any>active, 'active');
        }

        let localVarHeaders = this.defaultHeaders;

        let localVarCredential: string | undefined;
        // authentication (OpenID) required
        localVarCredential = this.configuration.lookupCredential('OpenID');
        if (localVarCredential) {
        }

        let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (localVarHttpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (localVarHttpHeaderAcceptSelected !== undefined) {
            localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
        }



        let responseType_: 'text' | 'json' | 'blob' = 'json';
        if (localVarHttpHeaderAcceptSelected) {
            if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
                responseType_ = 'text';
            } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
                responseType_ = 'json';
            } else {
                responseType_ = 'blob';
            }
        }

        let localVarPath = `/users/userInfo`;
        return this.httpClient.request<Array<UserInfo>>('get', `${this.configuration.basePath}${localVarPath}`,
            {
                params: localVarQueryParameters,
                responseType: <any>responseType_,
                withCredentials: this.configuration.withCredentials,
                headers: localVarHeaders,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Used by the administrators in order to update a specific INTERNAL user total space or admin status
     * @param userID The id of the user
     * @param userInfo 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public putUserUserInfo(userID: string, userInfo: UserInfo, observe?: 'body', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<UserInfo>;
    public putUserUserInfo(userID: string, userInfo: UserInfo, observe?: 'response', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<HttpResponse<UserInfo>>;
    public putUserUserInfo(userID: string, userInfo: UserInfo, observe?: 'events', reportProgress?: boolean, options?: {httpHeaderAccept?: 'application/json',}): Observable<HttpEvent<UserInfo>>;
    public putUserUserInfo(userID: string, userInfo: UserInfo, observe: any = 'body', reportProgress: boolean = false, options?: {httpHeaderAccept?: 'application/json',}): Observable<any> {
        if (userID === null || userID === undefined) {
            throw new Error('Required parameter userID was null or undefined when calling putUserUserInfo.');
        }
        if (userInfo === null || userInfo === undefined) {
            throw new Error('Required parameter userInfo was null or undefined when calling putUserUserInfo.');
        }

        let localVarHeaders = this.defaultHeaders;

        let localVarCredential: string | undefined;
        // authentication (OpenID) required
        localVarCredential = this.configuration.lookupCredential('OpenID');
        if (localVarCredential) {
        }

        let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (localVarHttpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/json'
            ];
            localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (localVarHttpHeaderAcceptSelected !== undefined) {
            localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
        }



        // to determine the Content-Type header
        const consumes: string[] = [
            'application/json'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected !== undefined) {
            localVarHeaders = localVarHeaders.set('Content-Type', httpContentTypeSelected);
        }

        let responseType_: 'text' | 'json' | 'blob' = 'json';
        if (localVarHttpHeaderAcceptSelected) {
            if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
                responseType_ = 'text';
            } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
                responseType_ = 'json';
            } else {
                responseType_ = 'blob';
            }
        }

        let localVarPath = `/user/${this.configuration.encodeParam({name: "userID", value: userID, in: "path", style: "simple", explode: false, dataType: "string", dataFormat: undefined})}/userInfo`;
        return this.httpClient.request<UserInfo>('put', `${this.configuration.basePath}${localVarPath}`,
            {
                body: userInfo,
                responseType: <any>responseType_,
                withCredentials: this.configuration.withCredentials,
                headers: localVarHeaders,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}
