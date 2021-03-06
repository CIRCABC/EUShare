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
/* tslint:disable:no-unused-variable member-ordering */

export class BearerToken {
    access_token!: string;
    token_type!: string;
    expires_in!: number;
    scope!: string;
    actor!: Actor;
}

export class Actor {
    azp!: string;
}

import { Inject, Injectable, Optional } from '@angular/core';
import {
    HttpClient, HttpHeaders, HttpParams,
    HttpResponse, HttpEvent, HttpParameterCodec
} from '@angular/common/http';
import { CustomHttpParameterCodec } from '../encoder';
import { Observable, Subject } from 'rxjs';

import { environment } from '../../../environments/environment';
import { BASE_PATH } from '../variables';
import { Configuration } from '../configuration';
import { Router } from '@angular/router';
import { UserInfo } from '../model/userInfo';
import { KeyStoreService } from '../../services/key-store.service';
import { OAuthService } from 'angular-oauth2-oidc';


@Injectable({
    providedIn: 'root'
})
export class SessionService {

    protected basePath = 'http://localhost:8888';

    public logout() {
        sessionStorage.removeItem('ES_AUTH');
        sessionStorage.removeItem('ES_USERINFO');
        this.router.navigateByUrl('');
    }
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();
    public encoder: HttpParameterCodec;

    public userInfoSubject: Subject<UserInfo> = new Subject<UserInfo>();
    public userInfo$ = this.userInfoSubject.asObservable();

    public getStoredUserInfo(): UserInfo | null {
        let userInfoStringified = sessionStorage.getItem('ES_USERINFO');
        if (userInfoStringified) {
            const userInfo = JSON.parse(userInfoStringified);
            this.userInfoSubject.next(userInfo);
            return userInfo;
        }
        return null;
    }

    public getStoredIsAdmin(): boolean | null {
        const userInfo = this.getStoredUserInfo();
        if (userInfo) {
            return userInfo.isAdmin;
        }
        return null;
    }

    public getStoredId(): string | null {
        const userInfo = this.getStoredUserInfo();
        if (userInfo) {
            return userInfo.id;
        }
        return null;
    }

    public getStoredName(): string | null {
        const userInfo = this.getStoredUserInfo();
        if (userInfo) {
            return userInfo.givenName;
        }
        return null;
    }

    public setStoredUserInfo(userInfo: UserInfo) {
        this.userInfoSubject.next(userInfo);
        sessionStorage.setItem('ES_USERINFO', JSON.stringify(userInfo));
    }

    public getAccessToken(): Observable<BearerToken> {
        let headers = new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded');
        headers = headers.set('Accept', 'application/json');

        const now = Date.now();
        const assertionData = {
            'iss': environment.OIDC_CLIENTID, // iss must be equal to sub and must be the ClientID of the SPA
            'sub': environment.OIDC_CLIENTID,
            'aud': environment.OIDC_TOKENENDPOINT,   // aud must be the URI of the EU Login end-point + '/token'
            'jti': this.keystoreService.randomHex(64), // a unique, random and unguessable value
            'exp': now + (5 * 60 * 1000), // expiration time, e.g. now + 5 minutes
            'iat': now, // the issuing time
            'id_token': this.oAuthService.getIdToken() // the token acquired during the authentication
        };
        const assertionJws = this.keystoreService.signJWT(assertionData);

        const params = new HttpParams()
            .set('grant_type', 'urn:ietf:params:oauth:grant-type:jwt-bearer')
            .set('client_id', environment.OIDC_CLIENTID)
            .set('assertion', assertionJws)
            .set('scope', 'openid email')
            .set('audience', environment.OIDC_BACKEND_CLIENTID);

        // com.nimbusds.oauth2.sdk.ParseException: The HTTP Content-Type header must be application/x-www-form-urlencoded; charset=UTF-8
        return this.httpClient
            .post<BearerToken>(
                environment.OIDC_TOKENENDPOINT,
                null,
                {
                    headers: headers,
                    params: params,
                    withCredentials: true
                });
    }

    constructor(private oAuthService: OAuthService, private keystoreService: KeyStoreService, protected httpClient: HttpClient, @Optional() @Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration, private router: Router) {

        if (configuration) {
            this.configuration = configuration;
        }
        if (typeof this.configuration.basePath !== 'string') {
            if (typeof basePath !== 'string') {
                basePath = this.basePath;
            }
            this.configuration.basePath = basePath;
        }
        this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
    }



    /**
     * Used to login by internal users
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public postLogin(observe?: 'body', reportProgress?: boolean): Observable<string>;
    public postLogin(observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<string>>;
    public postLogin(observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<string>>;
    public postLogin(observe: any = 'body', reportProgress: boolean = false): Observable<any> {

        let headers = this.defaultHeaders;

        // authentication (openId) required
        // to determine the Accept header
        const httpHeaderAccepts: string[] = [
            'text/plain',
            //'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }


        return this.httpClient.post(`${this.configuration.basePath}/login`,
            null,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress,
                responseType: 'text'
            }
        );
    }

}
