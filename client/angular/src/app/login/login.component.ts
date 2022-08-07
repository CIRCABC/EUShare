/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { KeyStoreService } from '../services/key-store.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {
  constructor(
    private oauthService: OAuthService,
    private keyStoreService: KeyStoreService
  ) {
    localStorage.removeItem("ES_USERINFO");
    localStorage.removeItem("id_token");
    localStorage.removeItem("id_token_claims_obj");
    localStorage.removeItem("id_token_expires_at");
    localStorage.removeItem("nonce");
    localStorage.removeItem("session_state");
  }

  ngOnInit() {
    localStorage.removeItem("ES_USERINFO");
    localStorage.removeItem("id_token");
    localStorage.removeItem("id_token_claims_obj");
    localStorage.removeItem("id_token_expires_at");
    localStorage.removeItem("nonce");
    localStorage.removeItem("session_state");
  }

  login() {



    this.keyStoreService.prepareKeyStore();
    const customQueryParams: { [key: string]: any } = {};
    customQueryParams['req_cnf'] =
      this.keyStoreService.publicJWKBase64UrlEncoded();
    this.oauthService.customQueryParams = customQueryParams;

    this.oauthService.initImplicitFlow();

  }

  euLoginCreate() {
    window.location.href =
      'https://ecas.cc.cec.eu.int:7002/cas/eim/external/register.cgi';
  }
}
