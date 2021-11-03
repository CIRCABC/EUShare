/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { I18nService } from '../common/i18n/i18n.service';
import { NotificationService } from '../common/notification/notification.service';
import { Status } from '../openapi';

@Injectable({
  providedIn: 'root',
})
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(
    private notificationService: NotificationService,
    private i18nService: I18nService
  ) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((err) => {
        console.log(err);
        if (err instanceof HttpErrorResponse) {
          const isPostLogin =
            req.url.includes('/login') && req.method === 'POST';
          const isGetUsersUserInfo =
            req.url.includes('/users/userInfo') && req.method === 'GET';
          const isPutUserUserInfo =
            req.url.includes('/user') &&
            req.url.includes('/userInfo') &&
            req.method === 'PUT';
          const isGetUserUserInfo =
            req.url.includes('/user') &&
            !req.url.includes('/users') &&
            req.url.includes('/userInfo') &&
            req.method === 'GET';
          const isGetFilesFileInfoUploader =
            req.url.includes('/user') &&
            req.url.includes('/files/fileInfoUploader') &&
            req.method === 'GET';
          const isGetFilesFileInfoRecipient =
            req.url.includes('/user') &&
            req.url.includes('/files/fileInfoRecipient') &&
            req.method === 'GET';
          const isGetFile = req.url.includes('/file/') && req.method === 'GET';
          const isDeleteFile =
            req.url.includes('/file/') &&
            !req.url.includes('/fileRequest/sharedWith') &&
            req.method === 'DELETE';
          const isPostFileFileRequest =
            req.url.includes('/file/fileRequest') && req.method === 'POST';
          const isPostFileSharedWith =
            req.url.includes('/fileRequest/sharedWith') &&
            req.method === 'POST';
          const isPostFileContent =
            req.url.includes('/fileRequest/fileContent') &&
            req.method === 'POST';
          const isDeleteFileSharedWithUser =
            req.url.includes('/fileRequest/sharedWith') &&
            req.method === 'DELETE';

          let action = `${this.i18nService.to()} ?`;

          if (isPostLogin) {
            action = `${this.i18nService.to()} ${this.i18nService.translate(
              'login'
            )}.`;
          }
          if (isGetUsersUserInfo) {
            action = `${this.i18nService.to()} ${this.i18nService.translate(
              'retrieve.users.info'
            )}.`;
          }
          if (isPutUserUserInfo) {
            action = `${this.i18nService.to()} ${this.i18nService.translate(
              'modify.user.rights'
            )}.`;
          }
          if (isGetUserUserInfo) {
            action = `${this.i18nService.to()} ${this.i18nService.translate(
              'retrieve.user.info'
            )}.`;
          }
          if (isGetFilesFileInfoUploader) {
            action = `${this.i18nService.to()} ${this.i18nService.translate(
              'access.uploaded.files'
            )}.`;
          }
          if (isGetFilesFileInfoRecipient) {
            action = `${this.i18nService.to()} ${this.i18nService.translate(
              'access.shared.files'
            )}.`;
          }
          if (isGetFile) {
            action = `${this.i18nService.to()} ${this.i18nService.translate(
              'download.file'
            )}.`;
          }
          if (isDeleteFile) {
            action = `${this.i18nService.to()} ${this.i18nService.translate(
              'delete.file'
            )}.`;
          }
          if (isPostFileFileRequest) {
            action = `${this.i18nService.to()} ${this.i18nService.translate(
              'upload.file'
            )}.`;
          }
          if (isPostFileSharedWith) {
            action = `${this.i18nService.to()} ${this.i18nService.translate(
              'add.recipient.to.file'
            )}.`;
          }
          if (isPostFileContent) {
            action = `${this.i18nService.to()} ${this.i18nService.translate(
              'post.file'
            )}.`;
          }
          if (isDeleteFileSharedWithUser) {
            action = `${this.i18nService.to()} ${this.i18nService.translate(
              'delete.recipient'
            )}.`;
          }

          switch (err.status) {
            case 0: {
              this.notificationService.addErrorMessage(
                `${this.i18nService.translate(
                  'server.not.reachable'
                )} ${this.i18nService.whileTrying()} ${action} ${this.i18nService.contactSupport()}`
              );
              break;
            }
            case 400: {
              this.notificationService.addErrorMessage(
                `${this.i18nService.translate(
                  'bad.request'
                )} ${this.i18nService.whileTrying()} ${action} ${this.i18nService.contactSupport()}`
              );
              break;
            }
            case 401: {
              if (!isGetFile) {
                this.notificationService.addErrorMessageTranslation(
                  'invalid.token.'
                );
              } else {
                this.notificationService.addErrorMessageTranslation(
                  'wrong.password'
                );
              }
              break;
            }
            case 403: {
              let errorMessage = `${this.i18nService.translate(
                'not.authorized'
              )} ${action}`;
              if (err.error) {
                const status: Status = JSON.parse(err.error);
                if (status.message) {
                  errorMessage += `${this.i18nService.translate(
                    'reason'
                  )} ${this.separateStatusMessage(status.message)}`;
                }
              }
              this.notificationService.addErrorMessage(errorMessage);
              break;
            }
            case 404: {
              let errorMessage = '';
              if (
                isPutUserUserInfo ||
                isGetUserUserInfo ||
                isGetFilesFileInfoUploader ||
                isGetFilesFileInfoRecipient
              ) {
                errorMessage += this.i18nService.translate('user.not.found');
              }
              if (
                isGetFile ||
                isDeleteFile ||
                isPostFileSharedWith ||
                isPostFileContent
              ) {
                errorMessage += this.i18nService.translate('file.not.found');
              }
              if (isDeleteFileSharedWithUser) {
                if (err.error) {
                  const status: Status = JSON.parse(err.error);
                  if (status.message) {
                    errorMessage += this.separateStatusMessage(status.message);
                  }
                }
              }
              errorMessage += ` ${this.i18nService.whileTrying()}  ${action}`;
              this.notificationService.addErrorMessage(errorMessage);
              break;
            }
            case 500: {
              this.notificationService.addErrorMessage(
                `${this.i18nService.translate(
                  'unexpected.error'
                )} ${action} ${this.i18nService.contactSupport()}`
              );
              break;
            }
            default: {
              this.notificationService.addErrorMessage(
                `${this.i18nService.translate(
                  'unexpected.error'
                )} ${action} ${this.i18nService.contactSupport()} ${this.i18nService.translate(
                  'error.code'
                )} ${err.status}`
              );
              break;
            }
          }
        }
        const error = err.error.message || err.statusText;
        return throwError(() => new Error(error));
      })
    );
  }

  public separateStatusMessage(message: string): string {
    let returnString = '';

    for (let i = 0; i < message.length; i++) {
      if (i > 1 && message.charAt(i) === message.charAt(i).toUpperCase()) {
        // eslint-disable-next-line prefer-template
        returnString += ' ' + message.charAt(i).toLowerCase();
      } else {
        returnString += message.charAt(i);
      }
    }

    return returnString;
  }
}
