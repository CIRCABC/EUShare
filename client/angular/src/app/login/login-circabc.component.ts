/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { KeyStoreService } from '../services/key-store.service';

@Component({
  selector: 'app-logincircabc',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginCircabcComponent implements OnInit {
  constructor(
    private oauthService: OAuthService,
    private keyStoreService: KeyStoreService
  ) {
  }

  @ViewChild('aClick', { read: ElementRef }) aClick:
    | ElementRef<HTMLElement>
    | undefined;
  ngOnInit() { 

    setTimeout(() => {
      if (this.aClick) {
        this.aClick.nativeElement.click();
      }
    }, 200);

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
