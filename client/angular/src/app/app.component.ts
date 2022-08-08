/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component } from '@angular/core';
import { Router } from '@angular/router';
import {
  AuthConfig,
  NullValidationHandler,
  OAuthService,
} from 'angular-oauth2-oidc';
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
  constructor(private oauthService: OAuthService, private router: Router) {
    this.configureOAuth();
  }

  private async configureOAuth() {

    if (this.router.url.indexOf('/login')!=-1) {
      localStorage.removeItem("ES_USERINFO");
      localStorage.removeItem("id_token");
      localStorage.removeItem("id_token_claims_obj");
      localStorage.removeItem("id_token_expires_at");
      localStorage.removeItem("nonce");
      localStorage.removeItem("session_state");
    }

    this.oauthService.configure(authCodeFlowConfig);

    this.oauthService.tokenValidationHandler = new NullValidationHandler();

    this.oauthService.loadDiscoveryDocumentAndTryLogin();

  }
}
