/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import {
  faCloudDownloadAlt,
  faShare,
  faShareAlt,
  faUsers,
  faUsersCog,
  faSignOutAlt,
} from '@fortawesome/free-solid-svg-icons';
import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../../environments/environment';
import { SessionStorageService } from '../services/session-storage.service';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { TranslocoModule } from '@ngneat/transloco';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  standalone: true,
  imports: [RouterLink, FontAwesomeModule, RouterLinkActive, TranslocoModule],
})
export class NavbarComponent implements OnInit {
  public faUsers = faUsers;
  public faShare = faShare;
  public faUsersCog = faUsersCog;
  public faCloudDownloadAlt = faCloudDownloadAlt;
  public faSignOutAlt = faSignOutAlt;
  public faShareAlt = faShareAlt;
  public userName: string | null = null;
  public isAdmin: boolean | null = null;
  public isBurgerActive = false;
  public circabc_url: string = environment.circabc_url;

  constructor(
    private sessionService: SessionStorageService,
    private oAuthService: OAuthService,
    public router: Router,
  ) {}

  ngOnInit() {
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

  logout() {
    this.oAuthService.logOut(true);
    this.sessionService.logout();
  }

  get loggedIn(): boolean {
    return this.sessionService.getStoredUserInfo() !== null;
  }

  public toggleBurger() {
    this.isBurgerActive = !this.isBurgerActive;
  }
}
