/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { KeyStoreService } from '../services/key-store.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  constructor(
    private oauthService: OAuthService,
    private keyStoreService: KeyStoreService
  ) {}

  ngOnInit() {}

  async login() {
    this.keyStoreService.prepareKeyStore();
    const customQueryParams: { [key: string]: any } = {};
    customQueryParams[
      'req_cnf'
    ] = this.keyStoreService.publicJWKBase64UrlEncoded();
    this.oauthService.customQueryParams = customQueryParams;
    await this.oauthService.initImplicitFlow();
  }
}
