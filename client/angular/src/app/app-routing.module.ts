/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { APP_INITIALIZER, NgModule } from '@angular/core';
import { RouterModule, Routes, NoPreloading } from '@angular/router';
import { loginCanActivate } from './login.guard';
import { uploadSuccessCanActivate } from './upload-success.guard';
import { LoginComponent } from './login/login.component';
import { LoginCircabcComponent } from './login/login-circabc.component';
import { UploadComponent } from './upload/upload.component';
import { UploadSuccessComponent } from './upload-success/upload-success.component';
import { SharedWithMeComponent } from './files/shared-with-me/shared-with-me.component';
import { FilelinkComponent } from './filelink/filelink.component';
import { CallBackComponent } from './call-back/call-back.component';
import { TermsOfServiceComponent } from './terms-of-service/terms-of-service.component';
import {
  TRANSLOCO_CONFIG,
  TranslocoConfig,
  TranslocoService,
} from '@ngneat/transloco';
import { forkJoin } from 'rxjs';

export function preloadAllTranslocoLanguages(
  transloco: TranslocoService,
  config: TranslocoConfig
): Function {
  return () => {
    const allLanguages = config.availableLangs.map((lang) =>
      transloco.load(lang as string)
    );
    return forkJoin(allLanguages).toPromise();
  };
}

const appRoutes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'logincircabc',
    component: LoginCircabcComponent,
  },
  {
    path: 'callback',
    component: CallBackComponent,
  },
  {
    path: 'home',
    loadComponent: () =>
      import('./files/my-shared-files/my-shared-files.component').then(
        (m) => m.MySharedFilesComponent
      ),
    canActivate: [loginCanActivate],
  },
  {
    path: 'upload',
    component: UploadComponent,
    canActivate: [loginCanActivate],
  },
  {
    path: 'uploadSuccess',
    component: UploadSuccessComponent,
    canActivate: [loginCanActivate, uploadSuccessCanActivate],
  },
  {
    path: 'download',
    component: SharedWithMeComponent,
    canActivate: [loginCanActivate],
  },
  {
    path: 'fs/:id',
    component: FilelinkComponent,
  },
  {
    path: 'administration',
    loadComponent: () =>
      import('./administration/administration.component').then(
        (m) => m.AdministrationComponent
      ),
    canActivate: [loginCanActivate],
  },
  {
    path: 'administration/:userId/files',
    loadComponent: () =>
      import(
        './files/other-user-shared-files/other-user-shared-files.component'
      ).then((m) => m.OtherUserSharedFilesComponent),
    data: { userName: 'dummyUserName' },
    canActivate: [loginCanActivate],
  },
  {
    path: 'profile',
    loadComponent: () =>
      import('./profile/profile.component').then((m) => m.MyUserComponent),
    canActivate: [loginCanActivate],
  },
  {
    path: 'privacyStatement',
    loadComponent: () =>
      import('./privacy-statement/privacy-statement.component').then(
        (m) => m.PrivacyStatementComponent
      ),
  },
  {
    path: 'termsOfService',
    component: TermsOfServiceComponent,
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(appRoutes, {
      preloadingStrategy: NoPreloading,
      paramsInheritanceStrategy: 'emptyOnly',
      scrollPositionRestoration: 'disabled',
      enableTracing: false, // !environment.production
    }),
  ],
  exports: [RouterModule],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: preloadAllTranslocoLanguages,
      deps: [TranslocoService, TRANSLOCO_CONFIG],
      multi: true,
    },
  ],
})
export class AppRoutingModule {}
