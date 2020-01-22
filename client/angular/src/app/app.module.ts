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
import { CalendarModule } from 'primeng/components/calendar/calendar';
import { environment } from '../environments/environment';
import { AppComponent } from './app.component';
import { FileLinkModalComponent } from './common/modals/file-link-modal/file-link-modal.component';
import { DownloadModalComponent } from './common/modals/download-modal/download-modal.component';
import { NotificationSystemComponent } from './common/notification/notification-system.component';
import { NotificationComponent } from './common/notification/notification.component';
import { FilelinkComponent } from './filelink/filelink.component';
import { MySharedFilesComponent } from './files/my-shared-files/my-shared-files.component';
import { SharedWithMeComponent } from './files/shared-with-me/shared-with-me.component';
import { BasicAuthenticationInterceptor } from './interceptors/basic-authentication-interceptor';
import { HttpErrorInterceptor } from './interceptors/http-error-interceptor';
import { LoginGuard } from './login.guard';
import { LoginComponent } from './login/login.component';
import { NavbarComponent } from './navbar/navbar.component';
import { UploadComponent } from './upload/upload.component';
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
import { UploadedFileRowComponent } from './common/uploaded-file-row/uploaded-file-row.component';
import { OtherUserSharedFilesComponent } from './files/other-user-shared-files/other-user-shared-files.component';
import { ShareWithUsersModalComponent } from './common/modals/share-with-users-modal/share-with-users-modal.component';
import { FileRowContainerComponent } from './common/uploaded-file-row-container/uploaded-file-row-container.component';
import { DownloadFileRowContainerComponent } from './common/download-file-row-container/download-file-row-container.component';
import { DownloadFileRowComponent } from './common/download-file-row/download-file-row.component';
import { OAuthModule } from 'angular-oauth2-oidc';
import { CallBackComponent } from './call-back/call-back.component';
import { KeyStoreService } from './services/key-store.service';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { MyUserComponent } from './my-user/my-user.component';
import { PrivacyStatementComponent } from './privacy-statement/privacy-statement.component';
import { TermsOfServiceComponent } from './terms-of-service/terms-of-service.component';
import { DownloadCardComponent } from './common/download-card/download-card.component';

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
  declarations: [
    AppComponent,
    NavbarComponent,
    LoginComponent,
    UploadComponent,
    FileAccessorDirective,
    MySharedFilesComponent,
    NotificationComponent,
    NotificationSystemComponent,
    FilelinkComponent,
    DownloadModalComponent,
    FileLinkModalComponent,
    SharedWithMeComponent,
    DownloadButtonComponent,
    AddRecipientsModalComponent,
    EmailInputComponent,
    MessageTextAreaComponent,
    LinkInputComponent,
    AdministrationComponent,
    FileSizeFormatPipe,
    UploadedFileRowComponent,
    OtherUserSharedFilesComponent,
    ShareWithUsersModalComponent,
    FileRowContainerComponent,
    DownloadFileRowContainerComponent,
    DownloadFileRowComponent,
    CallBackComponent,
    HeaderComponent,
    FooterComponent,
    MyUserComponent,
    PrivacyStatementComponent,
    TermsOfServiceComponent,
    DownloadCardComponent
  ],
  imports: [
    ApiModule,
    BrowserModule,
    ReactiveFormsModule,
    CalendarModule,
    FormsModule,
    HttpClientModule,
    OAuthModule.forRoot({
      resourceServer: {
        allowedUrls: ['http://localhost:8888'],
        sendAccessToken: true
      }
    }),
    RouterModule.forRoot(routes),
    FontAwesomeModule,
    BrowserAnimationsModule
  ],
  providers: [
    KeyStoreService,
    { provide: BASE_PATH, useValue: environment.API_BASE_PATH },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: BasicAuthenticationInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
