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
  RouterStateSnapshot,
  Router,
  CanActivateFn,
} from '@angular/router';

export const uploadSuccessCanActivate: CanActivateFn = (
  _next: ActivatedRouteSnapshot,
  _state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  if (
    router.getCurrentNavigation()?.extras.state?.['data'] &&
    isFileInfoUploader(router.getCurrentNavigation()?.extras.state?.['data'])
  ) {
    return true;
  } else {
    return router.navigateByUrl('/upload');
  }
};

function isFileInfoUploader(object: any): boolean {
  return (
    'expirationDate' in object &&
    'hasPassword' in object &&
    'name' in object &&
    'size' in object &&
    'fileId' in object &&
    'sharedWith' in object
  );
}
