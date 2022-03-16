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
import { LoginGuard } from './login.guard';
import { LoginComponent } from './login/login.component';
import { UploadComponent } from './upload/upload.component';
import { AdministrationComponent } from './administration/administration.component';
import { OtherUserSharedFilesComponent } from './files/other-user-shared-files/other-user-shared-files.component';
import { CallBackComponent } from './call-back/call-back.component';
import { MyUserComponent } from './my-user/my-user.component';
import { PrivacyStatementComponent } from './privacy-statement/privacy-statement.component';
import { TermsOfServiceComponent } from './terms-of-service/terms-of-service.component';
import { UploadSucessComponent } from './upload-sucess/upload-sucess.component';
import { UploadSuccessGuard } from './upload-success.guard';
import { LoginCircabcComponent } from './login/loginCircabc.component';

const appRoutes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'logincircabc',
    component: LoginCircabcComponent
  },
  {
    path: 'home',
    component: MySharedFilesComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'upload',
    component: UploadComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'uploadSuccess',
    component: UploadSucessComponent,
    canActivate: [LoginGuard, UploadSuccessGuard]
  },
  {
    path: 'download',
    component: SharedWithMeComponent,
    canActivate: [LoginGuard]
  },
  {
    path: 'fs/:id',
    component: FilelinkComponent
  },
  {
    path: 'administration',
    component: AdministrationComponent,
    canActivate: [LoginGuard]

  },
  {
    path: 'administration/:userId/files',
    component: OtherUserSharedFilesComponent,
    data: { userName: 'dummyUserName' },
    canActivate: [LoginGuard]
  },
  {
    path: 'callback',
    component: CallBackComponent
  },
  {
    path: 'myUser',
    component: MyUserComponent
  },
  {
    path: 'privacyStatement',
    component: PrivacyStatementComponent
  },
  {
    path: 'termsOfService',
    component: TermsOfServiceComponent
  }
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
