/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Location } from '@angular/common';
import { Component, HostListener } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslocoModule } from '@ngneat/transloco';
import {
  AuthConfig,
  NullValidationHandler,
  OAuthService,
} from 'angular-oauth2-oidc';
import { environment } from '../environments/environment';
import { AddRecipientsModalComponent } from './common/modals/add-recipients-modal/add-recipients-modal.component';
import { ChangeExpirationDateModalComponent } from './common/modals/change-expiration-date-modal/change-expiration-date-modal.component';
import { DeleteConfirmModalComponent } from './common/modals/delete-confirm-modal/delete-confirm-modal.component';
import { DownloadModalComponent } from './common/modals/download-modal/download-modal.component';
import { FileLinkModalComponent } from './common/modals/file-link-modal/file-link-modal.component';
import { OverwriteConfirmModalComponent } from './common/modals/overwrite-confirm-modal/overwrite-confirm-modal.component';
import { ShareWithUsersModalComponent } from './common/modals/share-with-users-modal/share-with-users-modal.component';
import { StatisticsModalComponent } from './common/modals/statistics-modal/statistics-modal.component';
import { NotificationSystemComponent } from './common/notification/notification-system.component';
import { FooterComponent } from './footer/footer.component';
import { CbcHeaderComponent } from './header/cbc-header/cbc-header.component';
import { NavbarComponent } from './navbar/navbar.component';

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
  imports: [
    CbcHeaderComponent,
    NavbarComponent,
    ShareWithUsersModalComponent,
    StatisticsModalComponent,
    DownloadModalComponent,
    FileLinkModalComponent,
    AddRecipientsModalComponent,
    DeleteConfirmModalComponent,
    ChangeExpirationDateModalComponent,
    OverwriteConfirmModalComponent,
    NotificationSystemComponent,
    RouterOutlet,
    FooterComponent,
    TranslocoModule,
  ],
  standalone: true,
})
export class AppComponent {
  timestamp: number;

  @HostListener('window:beforeunload', ['$event'])
  clearLocalStorage() {
    const timestamp = localStorage.getItem('timestamp');
    if (timestamp !== null && Number(timestamp) !== this.timestamp) {
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
