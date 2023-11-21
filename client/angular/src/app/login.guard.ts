/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { SessionStorageService } from './services/session-storage.service';

export const loginCanActivate: CanActivateFn = (
  _next: ActivatedRouteSnapshot,
  _state: RouterStateSnapshot
) => {
  const oAuthService = inject(OAuthService);
  const router = inject(Router);
  const sessionService = inject(SessionStorageService);
  if (
    oAuthService.getIdToken() !== null &&
    oAuthService.hasValidIdToken() &&
    sessionService.getStoredUserInfo() !== null
  ) {
    return true;
  } else {
    return router.navigate(['/login']);
  }
};
