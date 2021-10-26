/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component } from '@angular/core';
import {
  AuthConfig,
  NullValidationHandler,
  OAuthService,
} from 'angular-oauth2-oidc';
import { environment } from '../environments/environment';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { I18nService } from './common/i18n/i18n.service';

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
  public showRightDownload = false;

  constructor(
    private oauthService: OAuthService,
    private router: Router,
    private i18nService: I18nService
  ) {
    this.routerHelpForDownloadsBox();
    this.configureOAuth();
    this.i18nService.configureI18n();
  }

  private async configureOAuth() {
    this.oauthService.configure(authCodeFlowConfig);
    this.oauthService.tokenValidationHandler = new NullValidationHandler();
    await this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  private routerHelpForDownloadsBox() {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((nextEvent) => {
        const nextEventNavigationEnd: NavigationEnd = <NavigationEnd>nextEvent;
        const urlAfterRedirect = nextEventNavigationEnd.urlAfterRedirects;

        if (urlAfterRedirect === '/home' || urlAfterRedirect === '/download') {
          this.showRightDownload = true;
        } else {
          this.showRightDownload = false;
        }
      });
  }
}
