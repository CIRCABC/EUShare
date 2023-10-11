/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
/**
 * EU Captcha Rest API
 * API for use of EU Captcha
 *
 * The version of the OpenAPI document: 1.0
 * Contact: DIGIT-EU-CAPTCHA@ec.europa.eu
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
/* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional } from '@angular/core';
import {
  HttpClient,
  HttpHeaders,
  HttpParams,
  HttpResponse,
  HttpEvent,
  HttpParameterCodec,
} from '@angular/common/http';
import { CustomHttpParameterCodec } from '../encoder';
import { Observable } from 'rxjs';

import { CaptchaResultDto } from '../model/models';

import { BASE_PATH, COLLECTION_FORMATS } from '../variables';
import { Configuration } from '../configuration';

@Injectable({
  providedIn: 'root',
})
export class CaptchaControllerService {
  protected basePath = 'https://circabc.europa.eu/EuCaptcha-2.2.8';
  public defaultHeaders = new HttpHeaders();
  public configuration = new Configuration();
  public encoder: HttpParameterCodec;

  constructor(
    protected httpClient: HttpClient,
    @Optional() @Inject(BASE_PATH) basePath: string,
    @Optional() configuration: Configuration,
  ) {
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

  private addToHttpParams(
    httpParams: HttpParams,
    value: any,
    key?: string,
  ): HttpParams {
    if (typeof value === 'object' && value instanceof Date === false) {
      httpParams = this.addToHttpParamsRecursive(httpParams, value);
    } else {
      httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
    }
    return httpParams;
  }

  private addToHttpParamsRecursive(
    httpParams: HttpParams,
    value?: any,
    key?: string,
  ): HttpParams {
    if (value == null) {
      return httpParams;
    }

    if (typeof value === 'object') {
      if (Array.isArray(value)) {
        (value as any[]).forEach(
          (elem) =>
            (httpParams = this.addToHttpParamsRecursive(httpParams, elem, key)),
        );
      } else if (value instanceof Date) {
        if (key != null) {
          httpParams = httpParams.append(
            key,
            (value as Date).toISOString().substr(0, 10),
          );
        } else {
          throw Error('key may not be null if value is Date');
        }
      } else {
        Object.keys(value).forEach(
          (k) =>
            (httpParams = this.addToHttpParamsRecursive(
              httpParams,
              value[k],
              key != null ? `${key}.${k}` : k,
            )),
        );
      }
    } else if (key != null) {
      httpParams = httpParams.append(key, value);
    } else {
      throw Error('key may not be null if value is not object or array');
    }
    return httpParams;
  }

  /**
   * Get a Captcha image
   * Returns a captcha image as per locale, captchaLength, type and capitalization or not
   * @param locale locale
   * @param captchaLength captchaLength
   * @param captchaType captchaType
   * @param capitalized capitalized
   * @param degree degree
   * @param userId userId
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getCaptchaImageUsingGET(
    locale?: string,
    captchaLength?: number,
    captchaType?: string,
    capitalized?: boolean,
    degree?: number,
    userId?: string,
    observe?: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<CaptchaResultDto>;
  public getCaptchaImageUsingGET(
    locale?: string,
    captchaLength?: number,
    captchaType?: string,
    capitalized?: boolean,
    degree?: number,
    userId?: string,
    observe?: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpResponse<CaptchaResultDto>>;
  public getCaptchaImageUsingGET(
    locale?: string,
    captchaLength?: number,
    captchaType?: string,
    capitalized?: boolean,
    degree?: number,
    userId?: string,
    observe?: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpEvent<CaptchaResultDto>>;
  public getCaptchaImageUsingGET(
    locale?: string,
    captchaLength?: number,
    captchaType?: string,
    capitalized?: boolean,
    degree?: number,
    userId?: string,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<any> {
    let queryParameters = new HttpParams({ encoder: this.encoder });
    if (locale !== undefined && locale !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>locale,
        'locale',
      );
    }
    if (captchaLength !== undefined && captchaLength !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>captchaLength,
        'captchaLength',
      );
    }
    if (captchaType !== undefined && captchaType !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>captchaType,
        'captchaType',
      );
    }
    if (capitalized !== undefined && capitalized !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>capitalized,
        'capitalized',
      );
    }
    if (degree !== undefined && degree !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>degree,
        'degree',
      );
    }
    if (userId !== undefined && userId !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>userId,
        'userId',
      );
    }

    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined =
      options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = ['*/*'];
      httpHeaderAcceptSelected =
        this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    let responseType: 'text' | 'json' = 'json';
    if (
      httpHeaderAcceptSelected &&
      httpHeaderAcceptSelected.startsWith('text')
    ) {
      responseType = 'text';
    }

    return this.httpClient.get<CaptchaResultDto>(
      `${this.configuration.basePath}/api/captchaImg`,
      {
        params: queryParameters,
        responseType: <any>responseType,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }

  /**
   * Refresh a previous Captcha image
   * Returns a new captcha image as per locale, captchaLength, type and capitalization or not
   * @param previousCaptchaId previousCaptchaId
   * @param locale locale
   * @param captchaLength captchaLength
   * @param captchaType captchaType
   * @param capitalized capitalized
   * @param degree degree
   * @param userId userId
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public reloadCaptchaImageUsingGET(
    previousCaptchaId: string,
    locale?: string,
    captchaLength?: number,
    captchaType?: string,
    capitalized?: boolean,
    degree?: number,
    userId?: string,
    observe?: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<CaptchaResultDto>;
  public reloadCaptchaImageUsingGET(
    previousCaptchaId: string,
    locale?: string,
    captchaLength?: number,
    captchaType?: string,
    capitalized?: boolean,
    degree?: number,
    userId?: string,
    observe?: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpResponse<CaptchaResultDto>>;
  public reloadCaptchaImageUsingGET(
    previousCaptchaId: string,
    locale?: string,
    captchaLength?: number,
    captchaType?: string,
    capitalized?: boolean,
    degree?: number,
    userId?: string,
    observe?: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpEvent<CaptchaResultDto>>;
  public reloadCaptchaImageUsingGET(
    previousCaptchaId: string,
    locale?: string,
    captchaLength?: number,
    captchaType?: string,
    capitalized?: boolean,
    degree?: number,
    userId?: string,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<any> {
    if (previousCaptchaId === null || previousCaptchaId === undefined) {
      throw new Error(
        'Required parameter previousCaptchaId was null or undefined when calling reloadCaptchaImageUsingGET.',
      );
    }

    let queryParameters = new HttpParams({ encoder: this.encoder });
    if (locale !== undefined && locale !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>locale,
        'locale',
      );
    }
    if (captchaLength !== undefined && captchaLength !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>captchaLength,
        'captchaLength',
      );
    }
    if (captchaType !== undefined && captchaType !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>captchaType,
        'captchaType',
      );
    }
    if (capitalized !== undefined && capitalized !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>capitalized,
        'capitalized',
      );
    }
    if (degree !== undefined && degree !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>degree,
        'degree',
      );
    }
    if (userId !== undefined && userId !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>userId,
        'userId',
      );
    }

    let headers = this.defaultHeaders;

    let httpHeaderAcceptSelected: string | undefined =
      options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = ['*/*'];
      httpHeaderAcceptSelected =
        this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    let responseType: 'text' | 'json' = 'json';
    if (
      httpHeaderAcceptSelected &&
      httpHeaderAcceptSelected.startsWith('text')
    ) {
      responseType = 'text';
    }

    return this.httpClient.get<CaptchaResultDto>(
      `${this.configuration.basePath}/api/reloadCaptchaImg/${encodeURIComponent(
        String(previousCaptchaId),
      )}`,
      {
        params: queryParameters,
        responseType: <any>responseType,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }

  /**
   * Validate a Captcha image
   * Returns success or failed as an answer
   * @param xJwtString captcha token retrieved from get in header response
   * @param captchaId captchaId
   * @param captchaAnswer captchaAnswer
   * @param useAudio useAudio
   * @param captchaType captchaType
   * @param userId userId
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public validateCaptchaUsingPOST(
    xJwtString: string,
    captchaId: string,
    captchaAnswer?: string,
    useAudio?: boolean,
    captchaType?: string,
    userId?: string,
    observe?: 'body',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<string>;
  public validateCaptchaUsingPOST(
    xJwtString: string,
    captchaId: string,
    captchaAnswer?: string,
    useAudio?: boolean,
    captchaType?: string,
    userId?: string,
    observe?: 'response',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpResponse<string>>;
  public validateCaptchaUsingPOST(
    xJwtString: string,
    captchaId: string,
    captchaAnswer?: string,
    useAudio?: boolean,
    captchaType?: string,
    userId?: string,
    observe?: 'events',
    reportProgress?: boolean,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<HttpEvent<string>>;
  public validateCaptchaUsingPOST(
    xJwtString: string,
    captchaId: string,
    captchaAnswer?: string,
    useAudio?: boolean,
    captchaType?: string,
    userId?: string,
    observe: any = 'body',
    reportProgress: boolean = false,
    options?: { httpHeaderAccept?: '*/*' },
  ): Observable<any> {
    if (xJwtString === null || xJwtString === undefined) {
      throw new Error(
        'Required parameter xJwtString was null or undefined when calling validateCaptchaUsingPOST.',
      );
    }
    if (captchaId === null || captchaId === undefined) {
      throw new Error(
        'Required parameter captchaId was null or undefined when calling validateCaptchaUsingPOST.',
      );
    }

    let queryParameters = new HttpParams({ encoder: this.encoder });
    if (captchaAnswer !== undefined && captchaAnswer !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>captchaAnswer,
        'captchaAnswer',
      );
    }
    if (useAudio !== undefined && useAudio !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>useAudio,
        'useAudio',
      );
    }
    if (captchaType !== undefined && captchaType !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>captchaType,
        'captchaType',
      );
    }
    if (userId !== undefined && userId !== null) {
      queryParameters = this.addToHttpParams(
        queryParameters,
        <any>userId,
        'userId',
      );
    }

    let headers = this.defaultHeaders;
    if (xJwtString !== undefined && xJwtString !== null) {
      headers = headers.set('x-jwtString', String(xJwtString));
    }

    let httpHeaderAcceptSelected: string | undefined =
      options && options.httpHeaderAccept;
    if (httpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = ['*/*'];
      httpHeaderAcceptSelected =
        this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (httpHeaderAcceptSelected !== undefined) {
      headers = headers.set('Accept', httpHeaderAcceptSelected);
    }

    let responseType: 'text' | 'json' = 'json';
    if (
      httpHeaderAcceptSelected &&
      httpHeaderAcceptSelected.startsWith('text')
    ) {
      responseType = 'text';
    }

    return this.httpClient.post<string>(
      `${this.configuration.basePath}/api/validateCaptcha/${encodeURIComponent(
        String(captchaId),
      )}`,
      null,
      {
        params: queryParameters,
        responseType: <any>responseType,
        withCredentials: this.configuration.withCredentials,
        headers: headers,
        observe: observe,
        reportProgress: reportProgress,
      },
    );
  }
}
