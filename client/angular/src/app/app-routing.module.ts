import { NgModule } from '@angular/core';
import { NoPreloading, PreloadAllModules, RouterModule, Routes } from '@angular/router';
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

const appRoutes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {
    path: 'login',
    component: LoginComponent
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
    path: 'filelink/:id/:filenameb64/:isPasswordProtected',
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
      paramsInheritanceStrategy: 'emptyOnly' ,
      scrollPositionRestoration: 'disabled',
      enableTracing: false // !environment.production
    })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
