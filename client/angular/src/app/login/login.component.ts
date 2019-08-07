/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationService } from '../common/notification/notification.service';
import {
  ApiModule,
  Credentials,
  SessionService,
  UsersService
} from '../openapi';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  id = '';
  password = '';
  constructor(
    private api: SessionService,
    private userApi: UsersService,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {}

  async login() {
    const credentials: Credentials = {
      email: this.id,
      password: this.password
    };
    try {
      const identifier = await this.api.postLogin(credentials).toPromise();
      this.api.setStoredCredentials(credentials);
      const userInfo = await this.userApi
        .getUserUserInfo(identifier)
        .toPromise();
      this.api.setStoredUserInfo(userInfo);
      this.router.navigateByUrl('home');
    } catch (error) {
      this.notificationService.errorMessageToDisplay(error, 'logging in');
    }
  }
}
