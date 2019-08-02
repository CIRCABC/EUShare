/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { faUpload, faUsers, faShare, faCloudDownloadAlt, faShareAlt } from '@fortawesome/free-solid-svg-icons';
import { SessionService, UserInfo } from '../openapi';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  public faUpload = faUpload;
  public faUsers = faUsers;
  public faShare = faShare;
  public faCloudDownloadAlt = faCloudDownloadAlt;
  public faShareAlt = faShareAlt;
  public userName: string | null = null;
  public isAdmin: boolean | null = null;

  constructor(private sessionService: SessionService) { }

  ngOnInit() {
    const userInfo = this.sessionService.getStoredUserInfo();
    if (userInfo) {
      this.userName = userInfo.name;
      this.isAdmin = userInfo.isAdmin;
    }

    this.sessionService.userInfo$.subscribe(userInfo => {
      this.userName = userInfo.name;
      this.isAdmin = userInfo.isAdmin;
    }, error => {

    });
  }

  logout() {
    this.sessionService.logout();
  }

  get loggedIn(): boolean {
    return this.sessionService.getStoredCredentials() !== null
  }
}
