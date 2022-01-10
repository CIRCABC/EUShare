/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Injectable } from '@angular/core';
import { map, timeout } from 'rxjs/operators';

import { FileService } from '../openapi';
import { HttpEvent, HttpEventType } from '@angular/common/http';
import { NotificationService } from '../common/notification/notification.service';
import { Observable, Subject } from 'rxjs';
import { saveAs } from 'file-saver';
import { I18nService } from '../common/i18n/i18n.service';

@Injectable({
  providedIn: 'root',
})
export class DownloadsService {



  constructor(
    private fileApi: FileService,
    private notificationService: NotificationService,
    private i18nService: I18nService
  ) {}




  public downloadAFile(
    fileId: string,
    fileName: string,
    inputPassword?: string
  ): Observable<DownloadInProgress> {


      const newDownloadObservable: Observable<DownloadInProgress> = this.fileApi
        .getFile(fileId, inputPassword, 'events', true)
        .pipe(map((event) => this.manageEventMessage(event, fileName, fileId)));
      

      return newDownloadObservable;
    
  }

  private error(fileId: string, message?: string): Error {
    if (message === undefined) {
      message = `An unknown error occurred while downloading the file. ${this.i18nService.contactSupport()}`;
    }
    // notification sent in the interceptor
    return new Error(fileId);
  }

  private manageEventMessage(
    event: HttpEvent<any>,
    fileName: string,
    fileId: string
  ): DownloadInProgress {
    const downloadValueToReturn: DownloadInProgress = {
      name: fileName,
      fileId,
      percentage: 0,
    };

    switch (event.type) {
      case HttpEventType.UploadProgress:
      case HttpEventType.Sent:
        return downloadValueToReturn;
      case HttpEventType.ResponseHeader:
        if (event.status === 200) {
          return downloadValueToReturn;
        }
        if (event.status === 400) {
          throw this.error(
            fileId,
            'The server could not find the file you are seeking to download. Please try again later or contact the support.'
          );
        }
        if (event.status === 401) {
          this.notificationService.addErrorMessageTranslation(
            'wrong.password',
            undefined,
            true
          );
          throw this.error(fileId, 'Wrong password, please try again.');
        }
        if (event.status === 403) {
          throw this.error(
            fileId,
            "It seems like you don't have the rights to access this file"
          );
        }
        if (event.status === 404) {
          throw this.error(fileId, 'File not found.');
        }
        if (event.status === 500) {
          throw this.error(
            fileId,
            `${this.i18nService.translate(
              'error.occurred.download'
            )} ${this.i18nService.contactSupport()}`
          );
        }
        throw this.error(fileId);

      case HttpEventType.DownloadProgress:
        let eventTotalOrUndefined = event.total;
        if (eventTotalOrUndefined === undefined) {
          eventTotalOrUndefined = 1;
        }
        const percentDone = Math.round(
          (event.loaded * 100) / eventTotalOrUndefined
        );
        downloadValueToReturn.percentage = percentDone;
        return downloadValueToReturn;

      case HttpEventType.Response:
        if (event.status === 200) {
          const file = event.body as Blob;
          saveAs(file, fileName);
          downloadValueToReturn.percentage = 100;
         
          return downloadValueToReturn;
        } else {
          // notification sent in error interceptor
          throw new Error(fileId);
        }

      default: {
        throw this.error(fileId);
      }
    }
  }
}

interface DownloadInProgressObservableWithMeta {
  downloadInProgressObservable: Observable<DownloadInProgress>;
  fileId: string;
}

export interface DownloadInProgress {
  name: string;
  fileId: string;
  percentage: number;
}
