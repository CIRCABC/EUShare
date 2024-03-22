/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { KeyStoreService } from '../services/key-store.service';
import { TranslocoModule } from '@ngneat/transloco';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [TranslocoModule],
})
export class LoginComponent {
  constructor(
    private oauthService: OAuthService,
    private keyStoreService: KeyStoreService,
  ) {}

  login() {
    this.keyStoreService.prepareKeyStore();
    const customQueryParams: { [key: string]: string } = {};
    customQueryParams['req_cnf'] =
      this.keyStoreService.publicJWKBase64UrlEncoded();
    this.oauthService.customQueryParams = customQueryParams;
    this.oauthService.initImplicitFlow();
  }

  euLoginCreate() {
    window.location.href =
      'https://ecas.cc.cec.eu.int:7002/cas/eim/external/register.cgi';
  }
}
