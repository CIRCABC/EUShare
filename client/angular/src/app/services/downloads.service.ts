/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';

import { FileService } from '../openapi';
import { HttpEvent, HttpEventType } from '@angular/common/http';
import { NotificationService } from '../common/notification/notification.service';
import { Observable } from 'rxjs';
import { saveAs } from 'file-saver';

@Injectable({
  providedIn: 'root',
})
export class DownloadsService {
  constructor(
    private fileApi: FileService,
    private notificationService: NotificationService
  ) {}

  public downloadAFile(
    fileId: string,
    fileName: string,
    inputPassword?: string
  ): Observable<DownloadInProgress> {
    return this.fileApi
      .getFile(fileId, inputPassword, 'events', true)
      .pipe(map((event) => this.manageEventMessage(event, fileName, fileId)));
  }

  private error(fileId: string): Error {
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
          throw this.error(fileId);
        }
        if (event.status === 401) {
          this.notificationService.addErrorMessageTranslation(
            'wrong.password',
            undefined,
            true
          );
          throw this.error(fileId);
        }
        if (event.status === 403) {
          throw this.error(fileId);
        }
        if (event.status === 404) {
          throw this.error(fileId);
        }
        if (event.status === 500) {
          throw this.error(fileId);
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

export interface DownloadInProgress {
  name: string;
  fileId: string;
  percentage: number;
}
