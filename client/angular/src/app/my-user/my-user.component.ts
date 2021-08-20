/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { SessionService, UsersService, UserInfo } from '../openapi';

@Component({
  selector: 'app-my-user',
  templateUrl: './my-user.component.html',
  styleUrls: ['./my-user.component.scss'],
})
export class MyUserComponent implements OnInit {
  public userInfo!: UserInfo;

  constructor(
    private sessionApi: SessionService,
    private userApi: UsersService
  ) {}

  async ngOnInit() {
    await this.initializeUserInfo();
  }

  async initializeUserInfo() {
    const id = this.sessionApi.getStoredId();
    const userInfoStored = this.sessionApi.getStoredUserInfo();
    if (userInfoStored) {
      this.userInfo = userInfoStored;
    }

    if (id) {
      try {
        this.userInfo = await this.userApi.getUserUserInfo(id).toPromise();
      } catch (error) {
        // managed in the interceptor
      }
    }
  }
}
