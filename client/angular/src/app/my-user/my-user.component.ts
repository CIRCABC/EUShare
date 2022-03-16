/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { UsersService, UserInfo } from '../openapi';
import { SessionStorageService } from '../services/session-storage.service';

@Component({
  selector: 'app-my-user',
  templateUrl: './my-user.component.html',
  styleUrls: ['./my-user.component.scss'],
})
export class MyUserComponent implements OnInit {
  public userInfo!: UserInfo;

  constructor(
    private sessionApi: SessionStorageService,
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
        this.userInfo = await firstValueFrom(this.userApi.getUserUserInfo(id));
      } catch (error) {
        // managed in the interceptor
      }
    }
  }
}
