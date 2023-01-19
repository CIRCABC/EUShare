/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, HostListener } from '@angular/core';
import {
  AuthConfig,
  NullValidationHandler,
  OAuthService,
} from 'angular-oauth2-oidc';
import { Location } from '@angular/common';
import { environment } from '../environments/environment';

const authCodeFlowConfig: AuthConfig = {
  // Url of the Identity Provider
  issuer: environment.OIDC_ISSUER,
  // URL of the SPA to redirect the user to after login
  redirectUri: environment.OIDC_REDIRECTURI,
  silentRefreshRedirectUri: `${window.location.origin}/tokenRefresh.html`,
  clientId: environment.OIDC_CLIENTID,
  requestAccessToken: false,

  responseType: 'id_token',

  scope: 'openid email',
  // disableAtHashCheck: true,
  showDebugInformation: false,
  sessionChecksEnabled: false,
  tokenEndpoint: environment.OIDC_TOKENENDPOINT,
};

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent {
  timestamp: number;

  @HostListener('window:beforeunload', ['$event'])
  clearLocalStorage() {

    let timestamp = localStorage.getItem('timestamp');
    if (timestamp != null && Number(timestamp) !== this.timestamp) {
      localStorage.removeItem('ES_AUTH');
      localStorage.removeItem('ES_USERINFO');
      localStorage.removeItem('id_token');
      localStorage.removeItem('id_token_claims_obj');
    }
  }

  constructor(
    private oauthService: OAuthService,
    private readonly location: Location
  ) {

    this.timestamp = new Date().getTime();
    localStorage.setItem('timestamp', this.timestamp.toString());

    this.configureOAuth();

  }

  private async configureOAuth() {


    this.oauthService.setStorage(localStorage);

    this.oauthService.configure(authCodeFlowConfig);

    this.oauthService.tokenValidationHandler = new NullValidationHandler();

    this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }
}
