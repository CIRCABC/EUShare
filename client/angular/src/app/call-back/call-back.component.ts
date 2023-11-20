/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EventType as OAuthEventType, OAuthService } from 'angular-oauth2-oidc';
import { UsersService, SessionService } from '../openapi';
import { NotificationService } from '../common/notification/notification.service';

import { firstValueFrom } from 'rxjs';
import { SessionStorageService } from '../services/session-storage.service';
import { TranslocoModule } from '@ngneat/transloco';

@Component({
  selector: 'app-call-back',
  templateUrl: './call-back.component.html',
  standalone: true,
  imports: [TranslocoModule],
})
export class CallBackComponent implements OnInit {
  constructor(
    private userService: UsersService,
    private notificationService: NotificationService,
    private sessionStorageService: SessionStorageService,
    private sessionService: SessionService,
    private oAuthService: OAuthService,
    private router: Router
  ) {}

  async ngOnInit() {
    this.oAuthService.events.subscribe((next) => {
      const nextType: OAuthEventType = next.type;

      switch (nextType) {
        case 'token_expires': {
          this.notificationService.addSuccessMessageTranslation(
            'session.expired'
          );
          this.sessionStorageService.logout();
          break;
        }
        case 'token_received':
        case 'token_refreshed':
        case 'silently_refreshed': {
          this.loginAndRedirect();
          break;
        }
        case 'silent_refresh_timeout':
        case 'discovery_document_loaded':
        case 'logout': {
          // nothing
          break;
        }
        default: {
          this.notificationService.addErrorMessageTranslation('oidc.error', {
            type: next.type,
          });
          break;
        }
      }
    });
  }

  public async loginAndRedirect() {
    try {
      const identifier = await firstValueFrom(this.sessionService.postLogin());
      const userInfo = await firstValueFrom(
        this.userService.getUserUserInfo(identifier.userId)
      );
      this.sessionStorageService.setStoredUserInfo(userInfo);
      this.router.navigateByUrl('upload');
    } catch (e) {
      // managed in the interceptor
    }
  }
}
