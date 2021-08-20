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
import { getBrowserLang, TranslocoService } from '@ngneat/transloco';

const authCodeFlowConfig: AuthConfig = {
  // Url of the Identity Provider
  issuer: environment.OIDC_ISSUER,
  // URL of the SPA to redirect the user to after login
  redirectUri: environment.OIDC_REDIRECTURI,
  silentRefreshRedirectUri: window.location.origin + '/tokenRefresh.html',
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
    private translateService: TranslocoService
  ) {
    this.routerHelpForDownloadsBox();
    this.configureOAuth();
    this.configureI18n();
  }

  private configureI18n() {
    // this language will be used as a fallback when a translation isn't found in the current language
    this.translateService.setDefaultLang('en');

    // the lang to use, if the lang isn't available, it will use the language from the browser
    // if the browser language could not be read, it will use english as language
    const browserLang = getBrowserLang();
    if (browserLang !== undefined) {
      this.translateService.setActiveLang(browserLang);
    } else {
      this.translateService.setActiveLang('en');
    }
  }

  private async configureOAuth() {
    this.oauthService.configure(authCodeFlowConfig);
    this.oauthService.tokenValidationHandler = new NullValidationHandler();
    await this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  private routerHelpForDownloadsBox() {
    this.router.events
      .pipe(
        filter((event) => {
          return event instanceof NavigationEnd;
        })
      )
      .subscribe((nextEvent) => {
        const nextEventNavigationEnd: NavigationEnd = <NavigationEnd>nextEvent;
        const urlAfterRedirect = nextEventNavigationEnd.urlAfterRedirects;

        if (
          urlAfterRedirect === '/login' ||
          urlAfterRedirect.startsWith('/filelink')
        ) {
          this.showRightDownload = false;
        }

        if (urlAfterRedirect === '/home') {
          this.showRightDownload = true;
        }
      });
  }
}
