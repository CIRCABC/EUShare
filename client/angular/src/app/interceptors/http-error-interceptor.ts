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
import { NotificationService } from '../common/notification/notification.service';
import { Status } from '../openapi';

@Injectable({
  providedIn: 'root',
})
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private notificationService: NotificationService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((err) => {
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

          let action = `${this.notificationService.to()} ?`;

          if (isPostLogin) {
            action = `${this.notificationService.to()} ${this.notificationService.translate(
              'login'
            )}.`;
          }
          if (isGetUsersUserInfo) {
            action = `${this.notificationService.to()} ${this.notificationService.translate(
              'retrieve.users.info'
            )}.`;
          }
          if (isPutUserUserInfo) {
            action = `${this.notificationService.to()} ${this.notificationService.translate(
              'modify.user.rights'
            )}.`;
          }
          if (isGetUserUserInfo) {
            action = `${this.notificationService.to()} ${this.notificationService.translate(
              'retrieve.user.info'
            )}.`;
          }
          if (isGetFilesFileInfoUploader) {
            action = `${this.notificationService.to()} ${this.notificationService.translate(
              'access.uploaded.files'
            )}.`;
          }
          if (isGetFilesFileInfoRecipient) {
            action = `${this.notificationService.to()} ${this.notificationService.translate(
              'access.shared.files'
            )}.`;
          }
          if (isGetFile) {
            action = `${this.notificationService.to()} ${this.notificationService.translate(
              'download.file'
            )}.`;
          }
          if (isDeleteFile) {
            action = `${this.notificationService.to()} ${this.notificationService.translate(
              'delete.file'
            )}.`;
          }
          if (isPostFileFileRequest) {
            action = `${this.notificationService.to()} ${this.notificationService.translate(
              'upload.file'
            )}.`;
          }
          if (isPostFileSharedWith) {
            action = `${this.notificationService.to()} ${this.notificationService.translate(
              'add.recipient.to.file'
            )}.`;
          }
          if (isPostFileContent) {
            action = `${this.notificationService.to()} ${this.notificationService.translate(
              'post.file'
            )}.`;
          }
          if (isDeleteFileSharedWithUser) {
            action = `${this.notificationService.to()} ${this.notificationService.translate(
              'delete.recipient'
            )}.`;
          }

          switch (err.status) {
            case 0: {
              this.notificationService.addErrorMessage(
                `${this.notificationService.translate(
                  'server.not.reachable'
                )} ${this.notificationService.whileTrying()} ${action} ${this.notificationService.contactSupport()}`
              );
              break;
            }
            case 400: {
              this.notificationService.addErrorMessage(
                `${this.notificationService.translate(
                  'bad.request'
                )} ${this.notificationService.whileTrying()} ${action} ${this.notificationService.contactSupport()}`
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
              let errorMessage = `${this.notificationService.translate(
                'not.authorized'
              )} ${action}`;
              if (err.error) {
                const status: Status = JSON.parse(err.error);
                if (status.message) {
                  errorMessage += `${this.notificationService.translate(
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
                errorMessage +=
                  this.notificationService.translate('user.not.found');
              }
              if (
                isGetFile ||
                isDeleteFile ||
                isPostFileSharedWith ||
                isPostFileContent
              ) {
                errorMessage +=
                  this.notificationService.translate('file.not.found');
              }
              if (isDeleteFileSharedWithUser) {
                if (err.error) {
                  const status: Status = JSON.parse(err.error);
                  if (status.message) {
                    errorMessage += this.separateStatusMessage(status.message);
                  }
                }
              }
              errorMessage += ` ${this.notificationService.whileTrying()}  ${action}`;
              this.notificationService.addErrorMessage(errorMessage);
              break;
            }
            case 500: {
              this.notificationService.addErrorMessage(
                `${this.notificationService.translate(
                  'unexpected.error'
                )} ${action} ${this.notificationService.contactSupport()}`
              );
              break;
            }
            default: {
              this.notificationService.addErrorMessage(
                `${this.notificationService.translate(
                  'unexpected.error'
                )} ${action} ${this.notificationService.contactSupport()} ${this.notificationService.translate(
                  'error.code'
                )} ${err.status}`
              );
              break;
            }
          }
        }
        const error = err.error.message || err.statusText;
        return throwError(error);
      })
    );
  }

  public separateStatusMessage(message: string): string {
    let returnString = '';
    const bigLetters = new Set();
    bigLetters
      .add('A')
      .add('B')
      .add('C')
      .add('D')
      .add('E')
      .add('F')
      .add('G')
      .add('H')
      .add('I')
      .add('J')
      .add('K')
      .add('L')
      .add('M')
      .add('N')
      .add('O')
      .add('P')
      .add('Q')
      .add('R')
      .add('S')
      .add('T')
      .add('U')
      .add('V')
      .add('W')
      .add('X')
      .add('Y')
      .add('Z');
    for (let i = 0; i < message.length; i++) {
      if (i > 1 && bigLetters.has(message.charAt(i))) {
        returnString += ' ' + message.charAt(i).toLowerCase();
      } else {
        returnString += message.charAt(i);
      }
    }

    return returnString;
  }
}
