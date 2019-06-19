/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SessionService, Credentials, ApiModule } from '../openapi';
import { NotificationService } from '../common/notification/notification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  id = '';
  password: string = '';
  constructor(private api: SessionService, private router: Router, private notificationService: NotificationService) { }

  ngOnInit() {
  }

  login() {
    let credentials: Credentials = {
      email: this.id,
      password: this.password
    }

    this.api.postLogin(credentials).toPromise().then(identifier => {
      this.api.setStoredCredentials(credentials);
      this.api.setStoredId(identifier);
      this.router.navigateByUrl('home');
    }).catch(error => {
      this.notificationService.errorMessageToDisplay(error, 'logging in');
    });
  }
}
