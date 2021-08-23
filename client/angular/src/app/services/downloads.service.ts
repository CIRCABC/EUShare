/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Injectable } from '@angular/core';
import { map } from 'rxjs/operators';

import { FileService } from '../openapi';
import { HttpEvent, HttpEventType } from '@angular/common/http';
import { NotificationService } from '../common/notification/notification.service';
import { Observable, Subject } from 'rxjs';
import { saveAs } from 'file-saver';

@Injectable({
  providedIn: 'root',
})
export class DownloadsService {
  private currentDownloadsInProgress = new Map<
    string,
    Observable<DownloadInProgress>
  >();
  private nextDownloadsInProgressSubject =
    new Subject<DownloadInProgressObservableWithMeta>();
  public nextDownloadsInProgress$: Observable<DownloadInProgressObservableWithMeta> =
    this.nextDownloadsInProgressSubject.asObservable();

  private displayDownloadsSubject = new Subject<boolean>();
  public displayDownloads$ = this.displayDownloadsSubject.asObservable();

  constructor(
    private fileApi: FileService,
    private notificationService: NotificationService
  ) {}

  public getCurrentObservables(): Observable<DownloadInProgress>[] {
    const observablesArray: Observable<DownloadInProgress>[] = [];
    this.currentDownloadsInProgress.forEach(
      (observale: Observable<DownloadInProgress>, _fileId: string) => {
        observablesArray.push(observale);
      }
    );
    return observablesArray;
  }

  public displayDownloadsBox() {
    this.displayDownloadsSubject.next(true);
  }

  public downloadAFile(
    fileId: string,
    fileName: string,
    inputPassword?: string,
    withSubcription?: boolean
  ): Observable<DownloadInProgress> {
    const currentDownloadInProgressOrUndefined =
      this.currentDownloadsInProgress.get(fileId);

    if (currentDownloadInProgressOrUndefined) {
      return currentDownloadInProgressOrUndefined;
    } else {
      const newDownloadObservable: Observable<DownloadInProgress> = this.fileApi
        .getFile(fileId, inputPassword, 'events', true)
        .pipe(map((event) => this.manageEventMessage(event, fileName, fileId)));
      this.currentDownloadsInProgress.set(fileId, newDownloadObservable);
      const downloadInProgressObservableWithMeta: DownloadInProgressObservableWithMeta =
        {
          downloadInProgressObservable: newDownloadObservable,
          fileId: fileId,
        };
      if (!withSubcription) {
        this.nextDownloadsInProgressSubject.next(
          downloadInProgressObservableWithMeta
        );
      }

      return newDownloadObservable;
    }
  }

  private error(fileId: string, message?: string): Error {
    if (message === undefined) {
      message = `An unknown error occurred while downloading the file. ${this.notificationService.contactSupport()}`;
    }
    // notification sent in the interceptor
    this.currentDownloadsInProgress.delete(fileId);
    return new Error(fileId);
  }

  private manageEventMessage(
    event: HttpEvent<any>,
    fileName: string,
    fileId: string
  ): DownloadInProgress {
    const downloadValueToReturn: DownloadInProgress = {
      name: fileName,
      fileId: fileId,
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
            `${this.notificationService.translate(
              'error.occurred.download'
            )} ${this.notificationService.contactSupport()}`
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
          this.currentDownloadsInProgress.delete(fileId);
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
