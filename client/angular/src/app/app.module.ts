/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
// tslint:disable-next-line:no-implicit-dependencies
import { environment } from '../environments/environment';
import { AppComponent } from './app.component';
import { FileLinkModalComponent } from './common/modals/file-link-modal/file-link-modal.component';
import { PasswordModalComponent } from './common/modals/password-modal/password-modal.component';
import { NotificationSystemComponent } from './common/notification/notification-system.component';
import { NotificationComponent } from './common/notification/notification.component';
import { FilelinkComponent } from './filelink/filelink.component';
import { MySharedFilesComponent } from './files/my-shared-files/my-shared-files.component';
import { SharedWithMeComponent } from './files/shared-with-me/shared-with-me.component';
import { BasicAuthenticationInterceptor } from './interceptors/basic-authentication-interceptor';
import { LoginGuard } from './login.guard';
import { LoginComponent } from './login/login.component';
import { NavbarComponent } from './navbar/navbar.component';
import { UploadComponent } from './upload/upload.component';
import { DateValueAccessor  } from './directives/date-value-accessor';
import { FileAccessorDirective } from './directives/file-accessor.directive';
import { ApiModule } from './openapi/api.module';
import { BASE_PATH, Configuration, ConfigurationParameters } from './openapi';
import { DownloadButtonComponent } from './common/buttons/download-button/download-button.component';
import { AddRecipientsModalComponent } from './common/modals/add-recipients-modal/add-recipients-modal.component';
import { EmailInputComponent } from './common/formComponents/email-input/email-input.component';
import { MessageTextAreaComponent } from './common/formComponents/message-text-area/message-text-area.component';
import { LinkInputComponent } from './common/formComponents/link-input/link-input.component';
import { AdministrationComponent } from './administration/administration.component';
import { FileSizeFormatPipe } from './common/pipes/file-size-format.pipe';

const routes: Routes = [
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
  }
];

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    LoginComponent,
    UploadComponent,
    FileAccessorDirective,
    DateValueAccessor,
    MySharedFilesComponent,
    NotificationComponent,
    NotificationSystemComponent,
    FilelinkComponent,
    PasswordModalComponent,
    FileLinkModalComponent,
    SharedWithMeComponent,
    DownloadButtonComponent,
    AddRecipientsModalComponent,
    EmailInputComponent,
    MessageTextAreaComponent,
    LinkInputComponent,
    AdministrationComponent,
    FileSizeFormatPipe
  ],
  imports: [
    ApiModule,
    BrowserModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    RouterModule.forRoot(routes),
    FontAwesomeModule,
    BrowserAnimationsModule
  ],
  providers: [
    { provide: BASE_PATH, useValue: environment.API_BASE_PATH },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: BasicAuthenticationInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
