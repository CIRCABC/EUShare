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
import { LoginComponent } from './login.component';
import { TranslocoModule } from '@ngneat/transloco';

@Component({
    selector: 'app-logincircabc',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    standalone: true,
    imports: [TranslocoModule],
})
export class LoginCircabcComponent extends LoginComponent implements OnInit {
  constructor(oauthService: OAuthService, keyStoreService: KeyStoreService) {
    super(oauthService, keyStoreService);
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
}
