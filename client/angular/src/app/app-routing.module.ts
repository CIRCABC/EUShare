/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { NgModule } from '@angular/core';
import { NoPreloading, RouterModule, Routes } from '@angular/router';
import { FilelinkComponent } from './filelink/filelink.component';
import { MySharedFilesComponent } from './files/my-shared-files/my-shared-files.component';
import { SharedWithMeComponent } from './files/shared-with-me/shared-with-me.component';
import { loginCanActivate } from './login.guard';
import { LoginComponent } from './login/login.component';
import { UploadComponent } from './upload/upload.component';
import { AdministrationComponent } from './administration/administration.component';
import { OtherUserSharedFilesComponent } from './files/other-user-shared-files/other-user-shared-files.component';
import { CallBackComponent } from './call-back/call-back.component';
import { MyUserComponent } from './profile/profile.component';
import { PrivacyStatementComponent } from './privacy-statement/privacy-statement.component';
import { TermsOfServiceComponent } from './terms-of-service/terms-of-service.component';
import { UploadSuccessComponent } from './upload-success/upload-success.component';
import { uploadSuccessCanActivate } from './upload-success.guard';
import { LoginCircabcComponent } from './login/login-circabc.component';

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
    path: 'home',
    component: MySharedFilesComponent,
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
    component: AdministrationComponent,
    canActivate: [loginCanActivate],
  },
  {
    path: 'administration/:userId/files',
    component: OtherUserSharedFilesComponent,
    data: { userName: 'dummyUserName' },
    canActivate: [loginCanActivate],
  },
  {
    path: 'callback',
    component: CallBackComponent,
  },
  {
    path: 'profile',
    component: MyUserComponent,
  },
  {
    path: 'privacyStatement',
    component: PrivacyStatementComponent,
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
})
export class AppRoutingModule {}
