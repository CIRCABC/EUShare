/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import {
  faCloudDownloadAlt,
  faShare,
  faShareAlt,
  faUsers,
  faUsersCog,
  faSignOutAlt,
} from '@fortawesome/free-solid-svg-icons';
import { SessionService } from '../openapi';
import { OAuthService } from 'angular-oauth2-oidc';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
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

  constructor(
    private sessionService: SessionService,
    private oAuthService: OAuthService
  ) {}

  ngOnInit() {
    const userInfo = this.sessionService.getStoredUserInfo();
    if (userInfo) {
      this.userName = userInfo.givenName;
      this.isAdmin = userInfo.isAdmin;
    }

    this.sessionService.userInfo$.subscribe(
      (secondUserInfo) => {
        this.userName = secondUserInfo.givenName;
        this.isAdmin = secondUserInfo.isAdmin;
      },
      (error) => {}
    );
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
