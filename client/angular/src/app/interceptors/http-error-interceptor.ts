/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit, Injector, Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { SessionService } from '../openapi';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: "root",
})
export class HttpErrorInterceptor implements HttpInterceptor {
    constructor(private sessionService: SessionService) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req).pipe(catchError(err => {
            if (err.status === 401) {
                // auto logout if 401 response returned from api
                this.sessionService.logout();
                location.reload(true);
            }
            
            const error = err.error.message || err.statusText;
            return throwError(error);
        }))
    }

}