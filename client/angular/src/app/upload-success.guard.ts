/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
  CanActivate,
} from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UploadSuccessGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(
    _next: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    if (
      this.router.getCurrentNavigation()?.extras.state?.['data'] &&
      this.isFileInfoUploader(
        this.router.getCurrentNavigation()?.extras.state?.['data']
      )
    ) {
      return true;
    } else {
      return this.router.navigateByUrl('/upload');
    }
  }
  isFileInfoUploader(object: any): boolean {
    return (
      'expirationDate' in object &&
      'hasPassword' in object &&
      'name' in object &&
      'size' in object &&
      'fileId' in object &&
      'sharedWith' in object
    );
  }
}
