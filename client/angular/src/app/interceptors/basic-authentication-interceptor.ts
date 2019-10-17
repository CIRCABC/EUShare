/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Component, Injectable, Injector, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { SessionService } from '../openapi';

@Injectable({
  providedIn: 'root'
})
export class BasicAuthenticationInterceptor implements HttpInterceptor {
  constructor(private inj: Injector, private sessionService: SessionService) {}
  intercept(
    req: HttpRequest<{}>,
    next: HttpHandler
  ): Observable<HttpEvent<{}>> {
    const storedCredentials = this.sessionService.getStoredCredentials();
    const isGetFile = req.url.includes('/file/') && req.method === 'GET';
    if (storedCredentials && !isGetFile) {
      req = req.clone({
        setHeaders: {
          Authorization:
            `Basic ` +
            btoa(`${storedCredentials.email}:${storedCredentials.password}`)
        }
      });
    }
    return next.handle(req);
  }
}
