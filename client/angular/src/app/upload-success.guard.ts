/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Router, CanActivate } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UploadSuccessGuard implements CanActivate {
  constructor(
    private router: Router,
  ) { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    if (
      
      this.router.getCurrentNavigation()// tslint:disable-next-line:no-non-null-assertion
      && this.router.getCurrentNavigation()!.extras // tslint:disable-next-line:no-non-null-assertion
      && this.router.getCurrentNavigation()!.extras.state  // tslint:disable-next-line:no-non-null-assertion
      && this.router.getCurrentNavigation()!.extras.state!.data // tslint:disable-next-line:no-non-null-assertion
      && this.isFileInfoUploader(this.router.getCurrentNavigation()!.extras.state!.data)) { // tslint:disable-next-line:no-non-null-assertion
      return true;
    } else {
      return this.router.navigateByUrl('/upload');
    }
  }
  isFileInfoUploader(object: any): boolean {
    return ('expirationDate' in object)
      && ('hasPassword' in object)
      && ('name' in object)
      && ('size' in object)
      && ('fileId' in object)
      && ('sharedWith' in object);
  }
}