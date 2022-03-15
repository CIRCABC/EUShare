/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';
import { SessionStorageService } from '../../services/session-storage.service';

@Component({
  selector: 'app-cbc-header',
  templateUrl: './cbc-header.component.html',
  styleUrls: ['./cbc-header.component.scss'],
})
export class CbcHeaderComponent implements OnInit {
  public circabc_url: string = environment.circabc_url;
  public userName: string | null = null;
  public isAdmin: boolean | null = null;

  constructor(
    private sessionService: SessionStorageService,
    private oAuthService: OAuthService
  ) { }

  ngOnInit() {
    const userInfo = this.sessionService.getStoredUserInfo();
    if (userInfo) {
      this.userName = userInfo.givenName as string;
      this.isAdmin = userInfo.isAdmin;
    }

    this.sessionService.userInfo$.subscribe({
      next: (secondUserInfo) => {
        this.userName = secondUserInfo.givenName as string;
        this.isAdmin = secondUserInfo.isAdmin;
      },
      error: (_error) => { },
    });
  }

  logout() {
    this.oAuthService.logOut(true);
    this.sessionService.logout();
  }

  get loggedIn(): boolean {
    return this.sessionService.getStoredUserInfo() !== null;
  }

  isGuest() {
    return true;
  }
}


