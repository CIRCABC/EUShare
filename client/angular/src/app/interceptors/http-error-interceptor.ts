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
  HttpErrorResponse
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { NotificationService } from '../common/notification/notification.service';
import { Status } from '../openapi';

@Injectable({
  providedIn: 'root'
})
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private notificationService: NotificationService) { }

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError(err => {
        if (err instanceof HttpErrorResponse) {
          const isPostLogin = req.url.includes('/login') && req.method === 'POST';
          const isGetUsersUserInfo = req.url.includes('/users/userInfo') && req.method === 'GET';
          const isPutUserUserInfo = req.url.includes('/user') && req.url.includes('/userInfo') && req.method === 'PUT';
          const isGetUserUserInfo = req.url.includes('/user') && !req.url.includes('/users') && req.url.includes('/userInfo') && req.method === 'GET';
          const isGetFilesFileInfoUploader = req.url.includes('/user') && req.url.includes('/files/fileInfoUploader') && req.method === 'GET';
          const isGetFilesFileInfoRecipient = req.url.includes('/user') && req.url.includes('/files/fileInfoRecipient') && req.method === 'GET';
          const isGetFile = req.url.includes('/file/') && req.method === 'GET';
          const isDeleteFile = req.url.includes('/file/') && !req.url.includes('/fileRequest/sharedWith') && req.method === 'DELETE';
          const isPostFileFileRequest = req.url.includes('/file/fileRequest') && req.method === 'POST';
          const isPostFileSharedWith = req.url.includes('/fileRequest/sharedWith') && req.method === 'POST';
          const isPostFileContent = req.url.includes('/fileRequest/fileContent') && req.method === 'POST';
          const isDeleteFileSharedWithUser = req.url.includes('/fileRequest/sharedWith') && req.method === 'DELETE';

          let action = ' to ?';

          if (isPostLogin) {
            action = ' to login.'
          }
          if (isGetUsersUserInfo) {
            action = ' to retrieve several users information.'
          }
          if (isPutUserUserInfo) {
            action = ' to modify this user\'s rights.';
          }
          if (isGetUserUserInfo) {
            action = ' to retrieve the user\'s information.';
          }
          if (isGetFilesFileInfoUploader) {
            action = ' to access the uploaded files.';
          }
          if (isGetFilesFileInfoRecipient) {
            action = ' to access the shared files.';
          }
          if (isGetFile) {
            action = ' to download this file.';
          }
          if (isDeleteFile) {
            action = ' to delete this file.';
          }
          if (isPostFileFileRequest) {
            action = ' to upload this file.';
          }
          if (isPostFileSharedWith) {
            action = ' to add this recipient to the shared file.'
          }
          if (isPostFileContent) {
            action = ' to post this file content.'
          }
          if (isDeleteFileSharedWithUser) {
            action = ' to delete this recipient from the shared file.'
          }

          switch (err.status) {
            case 0: {
              this.notificationService.addErrorMessage(
                'Server is not reachable while trying' + action + ' Please contact the support.'
              );
              break;
            }
            case 400: {
              this.notificationService.addErrorMessage(
                'Bad request while trying' + action + ' Please contact the support.'
              );
              break;
            }
            case 401: {
              if (!isGetFile) {
                this.notificationService.addErrorMessage(
                  'Invalid ECAS token, please logout and login again.'
                );
              } else {
                this.notificationService.addErrorMessage(
                  'Wrong password.'
                );
              }
              break;
            }
            case 403: {
              let errorMessage = 'You are not authorized' + action;
              if (err.error) {
                const status: Status = JSON.parse(err.error);
                if (status.message) {
                  errorMessage += ' Reason: ' + this.separateStatusMessage(status.message);
                }
              }
              this.notificationService.addErrorMessage(errorMessage);
              break;
            }
            case 404: {
              let errorMessage = '';
              if (isPutUserUserInfo || isGetUserUserInfo || isGetFilesFileInfoUploader || isGetFilesFileInfoRecipient) {
                errorMessage += 'User not found';
              }
              if (isGetFile || isDeleteFile || isPostFileSharedWith || isPostFileContent) {
                errorMessage += 'File not found';
              }
              if (isDeleteFileSharedWithUser) {
                if (err.error) {
                  const status: Status = JSON.parse(err.error);
                  if (status.message) {
                    errorMessage += this.separateStatusMessage(status.message);
                  }
                }
              }
              errorMessage += ' while trying ' + action;
              this.notificationService.addErrorMessage(errorMessage);
              break;
            }
            case 500:
              {
                this.notificationService.addErrorMessage('An unexpected error occured on server side while trying' + action + ' Please contact the support.');
                break;
              }
            default:
              {
                this.notificationService.addErrorMessage('An unexpected error occured on server side while trying' + action + ' Please contact the support. Error code ' + err.status);
                break;
              }
          }

          if (err.status === 0) {

          }
          if (err.status === 400) {

          }
          if (err.status === 401) {

          }
          if (err.status === 403) {

          }

          if (err.status === 404) {

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
    bigLetters.add('A')
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
    for (var i = 0; i < message.length; i++) {
      if (i > 1 && bigLetters.has(message.charAt(i))) {
        returnString += ' ' + message.charAt(i).toLowerCase();
      } else {
        returnString += message.charAt(i);
      }
    }

    return returnString;
  }
}
