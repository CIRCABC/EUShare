/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { SessionService } from '../openapi';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private apiUrl = environment.backend_url;
  private _loggedIn = false;
  private _sessionId = '';
  private _userId?: string;
  private _username?: string;
  private _password?: string;

  constructor(private http: HttpClient, private router: Router) {}

  async login(userId: string): Promise<void> {
    if (this._loggedIn) {
      await this.logout();
    }

    try {
      this._sessionId = await this.http
        .post(`${this.apiUrl}/login`, userId, { responseType: 'text' })
        .toPromise();
      this._loggedIn = true;
      this._userId = userId;
      this.router.navigate(['/home']);
    } catch {
      this._loggedIn = false;
      this._userId = undefined;
      return Promise.reject('');
    }
  }

  async logout(): Promise<void> {
    if (this._sessionId === '') {
      return;
    }

    await this.http
      .post<void>(`${this.apiUrl}/logout`, undefined, {
        headers: { 'ES-Session': this._sessionId }
      })
      .toPromise();

    this.router.navigate(['/']);
  }

  public get loggedIn(): boolean {
    return this._loggedIn;
  }

  public get sessionId(): string {
    return this._sessionId;
  }

  public get userId(): string | undefined {
    return this._userId;
  }
}
