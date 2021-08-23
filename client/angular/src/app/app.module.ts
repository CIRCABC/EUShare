/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';

import { TranslocoModule } from '@ngneat/transloco';
import { TranslocoRootModule } from './transloco/transloco-root.module';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule, HammerModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
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
import { LoginComponent } from './login/login.component';
import { NavbarComponent } from './navbar/navbar.component';
import { UploadComponent } from './upload/upload.component';
import { FileAccessorDirective } from './directives/file-accessor.directive';
import { ApiModule } from './openapi/api.module';
import { BASE_PATH } from './openapi';
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
import { UploadSucessComponent } from './upload-sucess/upload-sucess.component';
import { CbcHeaderComponent } from './header/cbc-header/cbc-header.component';
import { CbcEcLogoAppComponent } from './header/cbc-ec-logo-app/cbc-ec-logo-app.component';
import { DeleteButtonComponent } from './common/buttons/delete-button/delete-button.component';
import { DeleteConfirmModalComponent } from './common/modals/delete-confirm-modal/delete-confirm-modal.component';
import { AppRoutingModule } from './app-routing.module';

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
    DeleteConfirmModalComponent,
    FileRowContainerComponent,
    DownloadFileRowContainerComponent,
    DownloadFileRowComponent,
    CallBackComponent,
    HeaderComponent,
    FooterComponent,
    MyUserComponent,
    PrivacyStatementComponent,
    TermsOfServiceComponent,
    DownloadCardComponent,
    UploadSucessComponent,
    CbcHeaderComponent,
    CbcEcLogoAppComponent,
    DeleteButtonComponent,
  ],
  imports: [
    AppRoutingModule,
    ApiModule,
    TranslocoModule,
    TranslocoRootModule,
    HammerModule,
    BrowserModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    OAuthModule.forRoot({
      resourceServer: {
        allowedUrls: ['https://localhost:8888'],
        sendAccessToken: true,
      },
    }),
    FontAwesomeModule,
    BrowserAnimationsModule,
  ],
  providers: [
    KeyStoreService,
    { provide: BASE_PATH, useValue: environment.API_BASE_PATH },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: BasicAuthenticationInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
