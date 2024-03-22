/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import {
  TranslocoModule,
  TranslocoService,
  getBrowserLang,
} from '@ngneat/transloco';
import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';
import { SessionStorageService } from '../../services/session-storage.service';
import { ReactiveFormsModule } from '@angular/forms';
import { CbcEcLogoAppComponent } from '../cbc-ec-logo-app/cbc-ec-logo-app.component';

@Component({
  selector: 'app-cbc-header',
  templateUrl: './cbc-header.component.html',
  styleUrls: ['./cbc-header.component.scss'],
  standalone: true,
  imports: [CbcEcLogoAppComponent, ReactiveFormsModule, TranslocoModule],
})
export class CbcHeaderComponent implements OnInit {
  public circabc_url: string = environment.circabc_url;
  public userName: string | null = null;
  public isAdmin: boolean | null = null;
  public defaultLang: string | undefined;

  constructor(
    private sessionService: SessionStorageService,
    private oAuthService: OAuthService,
    private translateService: TranslocoService,
  ) {}

  ngOnInit() {
    if (this.defaultLang === undefined) {
      this.defaultLang = getBrowserLang();
      this.selectLanguage(this.defaultLang);
    }

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
      error: (_error) => {}, // NOSONAR
    });
  }

  selectLanguage(lang: string | undefined) {
    this.defaultLang = lang;
    if (this.defaultLang !== undefined) {
      this.translateService.setActiveLang(this.defaultLang);
    } else {
      this.translateService.setActiveLang('en');
    }
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
