/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthConfig, NullValidationHandler, OAuthService } from 'angular-oauth2-oidc';

export const authCodeFlowConfig: AuthConfig = {
  // Url of the Identity Provider
  issuer: 'http://localhost:8080/auth/realms/implicit',
  // URL of the SPA to redirect the user to after login
  redirectUri: window.location.origin + '/callback',
  // The SPA's id. The SPA is registerd with this id at the auth-server
  // clientId: 'server.code',
  clientId: 'implicit-client',
  // Just needed if your auth server demands a secret. In general, this
  // is a sign that the auth server is not configured with SPAs in mind
  // and it might not enforce further best practices vital for security
  // such applications.
  // dummyClientSecret: 'secret',
  responseType: 'id_token',
  // set the scope for the permissions the client should request
  // The first four are defined by OIDC. 
  // Important: Request offline_access to get a refresh token
  // The api scope is a usecase specific one,
  silentRefreshRedirectUri: window.location.origin + '/tokenRefresh.html',

  //scope: 'openid profile email offline_access',
  scope: 'openid email',
  //disableAtHashCheck: true,
  showDebugInformation: true,
  // Not recommented:
  //disablePKCE: true
};


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  constructor(private oauthService: OAuthService, private router: Router) {
    this.configure();
  }

  private async configure() {
    this.oauthService.configure(authCodeFlowConfig);
    this.oauthService.tokenValidationHandler = new NullValidationHandler();
    await this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }


}
