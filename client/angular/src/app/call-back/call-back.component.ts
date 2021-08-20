/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { SessionService, UsersService } from '../openapi';
import { NotificationService } from '../common/notification/notification.service';

@Component({
  selector: 'app-call-back',
  templateUrl: './call-back.component.html',
  styleUrls: ['./call-back.component.scss'],
})
export class CallBackComponent implements OnInit {
  constructor(
    private userService: UsersService,
    private notificationService: NotificationService,
    private api: SessionService,
    private oAuthService: OAuthService,
    private router: Router
  ) {}

  async ngOnInit() {
    this.oAuthService.events.subscribe((next) => {
      const nextType: string = next.type;
      switch (nextType) {
        case 'token_expires': {
          this.notificationService.addSuccessMessageTranslation(
            'session.expired'
          );
          this.api.logout();
          break;
        }
        case 'token_received':
        case 'token_refreshed':
        case 'silently_refreshed': {
          console.log('silently_refreshed');
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
          this.notificationService.addErrorMessage(
            'Encountered an OIDC error of type ' + next.type
          );
          break;
        }
      }
    });
  }

  public async loginAndRedirect() {
    try {
      const identifier = await this.api.postLogin().toPromise();
      const userInfo = await this.userService
        .getUserUserInfo(identifier)
        .toPromise();
      this.api.setStoredUserInfo(userInfo);
      this.router.navigateByUrl('upload');
    } catch (e) {
      // managed in the interceptor
    }
  }
}
