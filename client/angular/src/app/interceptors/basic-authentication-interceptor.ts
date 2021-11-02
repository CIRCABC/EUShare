/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import {
  HttpClient,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { SessionServiceImpl } from '../openapi/api/session.service.impl';


@Injectable({
  providedIn: 'root',
})
export class BasicAuthenticationInterceptor implements HttpInterceptor {
  constructor(
    protected http: HttpClient,
    private sessionService: SessionServiceImpl,
    private oAuthService: OAuthService
  ) {}

  intercept(
    req: HttpRequest<{}>,
    next: HttpHandler
  ): Observable<HttpEvent<{}>> {
    const isSSOUrl = req.url.includes(environment.OIDC_ISSUER);
    const isOptions = req.method.includes('OPTIONS');
    if (isSSOUrl || isOptions) {
      return next.handle(req);
    }

    const isGetFile = req.url.includes('/file/') && req.method === 'GET';
    if (isGetFile) {
      req = req.clone({
        headers: req.headers.delete('Authorization'),
      });
      return next.handle(req);
    }
    // If there is no ID token, no need to try getting an Access Token, it will simply not work
    if (!this.oAuthService.getIdToken()) {
      return next.handle(req);
    }
    return this.sessionService.getAccessToken().pipe(
      switchMap((token) => {
        if (token) {
          req = req.clone({
            setHeaders: {
              Authorization: `Bearer ${token.access_token}`,
            },
          });
        }
        return next.handle(req);
      })
    );
  }
}
