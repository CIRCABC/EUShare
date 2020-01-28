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
import {
  HttpEvent,
  HttpEventType,
  HttpErrorResponse
} from '@angular/common/http';
import { NotificationService } from '../common/notification/notification.service';
import { Observable, Subject } from 'rxjs';
import { saveAs } from 'file-saver';

@Injectable({
  providedIn: 'root'
})
export class DownloadsService {
  private currentDownloadSubjects = new Map<
    string,
    Subject<DownloadInProgress>
  >();
  private nextDownloadsSubjectsSubject = new Subject<
    Subject<DownloadInProgress>
  >();
  public nextDownloadSubjects$ = this.nextDownloadsSubjectsSubject.asObservable();

  private displayDownloadsSubject = new Subject<boolean>();
  public displayDownloads$ = this.displayDownloadsSubject.asObservable();

  private displayArrowSubject = new Subject<boolean>();
  public displayArrow$ = this.displayArrowSubject.asObservable();

  constructor(
    private fileApi: FileService,
    private notificationService: NotificationService
  ) {}

  public getCurrentObservables(): Observable<DownloadInProgress>[] {
    const observablesArray: Observable<DownloadInProgress>[] = [];
    this.currentDownloadSubjects.forEach(
      (subject: Subject<DownloadInProgress>, fileId: string) => {
        observablesArray.push(subject.asObservable());
      }
    );
    return observablesArray;
  }

  public displayDownloadsBox() {
    this.displayDownloadsSubject.next(true);
  }

  public showDownloadArrow(show: boolean) {
    this.displayArrowSubject.next(show);
  }

  public downloadAFile(
    fileId: string,
    fileName: string,
    inputPassword?: string
  ): Observable<DownloadInProgress> {
    const currentDownloadSubjectOrUndefined = this.currentDownloadSubjects.get(
      fileId
    );

    if (currentDownloadSubjectOrUndefined) {
      return currentDownloadSubjectOrUndefined;
    } else {
      const newDownloadSubject = new Subject<DownloadInProgress>();
      this.currentDownloadSubjects.set(fileId, newDownloadSubject);
      this.nextDownloadsSubjectsSubject.next(newDownloadSubject);

      const firstDownloadValue: DownloadInProgress = {
        name: fileName,
        fileId: fileId,
        percentage: 0
      };
      newDownloadSubject.next(firstDownloadValue);

      this.fileApi
        .getFile(fileId, inputPassword, 'events', true)
        .pipe(map(event => this.manageEventMessage(event, fileName, fileId)))
        .subscribe();
      return newDownloadSubject.asObservable();
    }
  }

  private error(fileId: string, message: string) {
    const fileSubjectOrUndefined = this.currentDownloadSubjects.get(fileId);
    if (fileSubjectOrUndefined) {
      fileSubjectOrUndefined.error(fileId);
    }
    this.notificationService.addErrorMessage(message);
    this.currentDownloadSubjects.delete(fileId);
  }

  private next(fileId: string, downloadInProgress: DownloadInProgress) {
    const fileSubjectOrUndefined = this.currentDownloadSubjects.get(fileId);
    if (fileSubjectOrUndefined) {
      fileSubjectOrUndefined.next(downloadInProgress);
    }
    if (downloadInProgress.percentage === 100) {
      this.currentDownloadSubjects.delete(fileId);
    }
  }

  private manageEventMessage(
    event: HttpEvent<any>,
    fileName: string,
    fileId: string
  ) {
    const downloadValueToReturn: DownloadInProgress = {
      name: fileName,
      fileId: fileId,
      percentage: 0
    };

    switch (event.type) {
      case HttpEventType.Sent:
        return;

      case HttpEventType.UploadProgress:
        return;

      case HttpEventType.ResponseHeader:
        if (event.status === 400) {
          return this.error(
            fileId,
            'The server could not find the file you are seeking to download. Please try again later or contact the support.'
          );
        }
        if (event.status === 401) {
          return this.error(fileId, 'Wrong password, please try again.');
        }
        if (event.status === 403) {
          return this.error(
            fileId,
            "It seems like you don't have the rights to access this file"
          );
        }
        if (event.status === 404) {
          return this.error(fileId, 'File not found.');
        }
        if (event.status === 500) {
          return this.error(
            fileId,
            'An error occured while downloading the file. Please contact the support.'
          );
        }
        return;

      case HttpEventType.DownloadProgress:
        let eventTotalOrUndefined = event.total;
        if (eventTotalOrUndefined === undefined) {
          eventTotalOrUndefined = 1;
        }
        const percentDone = Math.round(
          (event.loaded * 100) / eventTotalOrUndefined
        );
        downloadValueToReturn.percentage = percentDone;
        return this.next(fileId, downloadValueToReturn);

      case HttpEventType.Response:
        if (event.status === 200) {
          const file = event.body as Blob;
          saveAs(file, fileName);
          downloadValueToReturn.percentage = 100;
          return this.next(fileId, downloadValueToReturn);
        } else {
          this.notificationService.errorMessageToDisplay(
            event.body as HttpErrorResponse,
            'downloading the file'
          );
          return this.error(fileId, 'downloading the file');
        }

      default:
        return this.error(
          fileId,
          'An error occured while downloading the file. Please contact the support.'
        );
    }
  }
}

export interface DownloadArrayAndMetaData {
  fileInfoUploaderArray: DownloadInProgress[];
}

export interface DownloadInProgress {
  name: string;
  fileId: string;
  percentage: number;
}
