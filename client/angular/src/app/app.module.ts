/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { AppComponent } from './app.component';
import { LoginGuard } from './login.guard';
import { LoginComponent } from './login/login.component';
import { NavbarComponent } from './navbar/navbar.component';
import { ApiService } from './service/api.service';
import { ReactiveFormsModule } from '@angular/forms';
import { UploadComponent } from './upload/upload.component';
import { FileAccessorDirective } from './directives/file-accessor.directive';
import { CalendarModule } from 'primeng/components/calendar/calendar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ApiModule } from './openapi/api.module';
import { BASE_PATH, Configuration, ConfigurationParameters } from './openapi';
import { environment } from '../environments/environment';
import { MySharedFilesComponent } from './files/my-shared-files/my-shared-files.component';
import { BasicAuthenticationInterceptor } from './interceptors/basic-authentication-interceptor';
import { NotificationComponent } from './common/notification/notification.component';
import { NotificationSystemComponent } from './common/notification/notification-system.component';
import { FilelinkComponent } from './filelink/filelink.component';
import { PasswordModalComponent } from './common/modals/password-modal/password-modal.component';
import { FileLinkModalComponent } from './common/modals/file-link-modal/file-link-modal.component';
import { SharedWithMeComponent } from './files/shared-with-me/shared-with-me.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {
    path: 'login',
    component: LoginComponent,
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
  }
];

@NgModule({
  declarations: [AppComponent, NavbarComponent,
      LoginComponent, UploadComponent, FileAccessorDirective, MySharedFilesComponent, NotificationComponent, NotificationSystemComponent, FilelinkComponent, PasswordModalComponent, FileLinkModalComponent, SharedWithMeComponent],
  imports: [
    ApiModule,
    BrowserModule,
    ReactiveFormsModule,
    CalendarModule,
    FormsModule,
    HttpClientModule,
    RouterModule.forRoot(routes),
    FontAwesomeModule,
    BrowserAnimationsModule],
  providers: [ApiService, 
    { provide: BASE_PATH, useValue: environment.API_BASE_PATH },
    { provide: HTTP_INTERCEPTORS, useClass: BasicAuthenticationInterceptor, multi: true },],
  bootstrap: [AppComponent]
})
export class AppModule {}
