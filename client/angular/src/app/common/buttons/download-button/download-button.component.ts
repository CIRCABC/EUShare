/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, Input, OnInit } from '@angular/core';
import { FileService } from '../../../openapi';
import { NotificationService } from '../../notification/notification.service';
import { saveAs } from 'file-saver';
import {
  HttpEvent,
  HttpEventType,
  HttpProgressEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { map, last } from 'rxjs/operators';

@Component({
  selector: 'app-download-button',
  templateUrl: './download-button.component.html',
  styleUrls: ['./download-button.component.css']
})
export class DownloadButtonComponent implements OnInit {
  // tslint:disable-next-line:no-input-rename
  @Input('fileId')
  private fileId!: string;

  // tslint:disable-next-line:no-input-rename
  @Input('fileName')
  public fileName!: string;

  // tslint:disable-next-line:no-input-rename
  @Input('isFileHasPassword')
  public isFileHasPassword = false;

  public isLoading = false;
  public percentageDownloaded = 0;
  public inputPassword = '';

  constructor(
    private notificationService: NotificationService,
    private fileApi: FileService
  ) { }

  ngOnInit() { }

  public download() {
    this.isLoading = true;
    this.fileApi
      .getFile(this.fileId, this.inputPassword, 'events', true)
      .pipe(map(event => this.getEventMessage(event)))
      .subscribe();
  }

  private getEventMessage(event: HttpEvent<any>) {
    switch (event.type) {
      case HttpEventType.Sent:
        return;

      case HttpEventType.UploadProgress:
        return;

      case HttpEventType.ResponseHeader:
        if (event.status === 400) {
          this.notificationService.addErrorMessage(
            'The server could not find the file you are seeking to download. Please try again later or contact the support.'
          );
          this.isLoading = false;
          this.percentageDownloaded = 0;
        }
        if (event.status === 401) {
          this.notificationService.addErrorMessage(
            'Wrong password, please try again.'
          );
          this.isLoading = false;
          this.percentageDownloaded = 0;
        }
        if (event.status === 404) {
          this.notificationService.addErrorMessage(
            'File not found.'
          );
          this.isLoading = false;
          this.percentageDownloaded = 0;
        }

        if (event.status === 500) {
          this.notificationService.addErrorMessage(
            'An error occured while downloading the file. Please contact the support.'
          );
          this.isLoading = false;
          this.percentageDownloaded = 0;
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
        this.percentageDownloaded = percentDone;
        return;

      case HttpEventType.Response:
        if (event.status === 200) {
          const file = event.body as Blob;
          saveAs(file, this.fileName);
          this.notificationService.addSuccessMessage(
            'File was completely downloaded!'
          );
        } else {
          this.notificationService.errorMessageToDisplay(
            event.body as HttpErrorResponse,
            'downloading the file'
          );
        }
        this.isLoading = false;
        this.percentageDownloaded = 0;
        return;

      default:
        this.notificationService.addErrorMessage(
          'An error occured while downloading the file. Please contact the support.'
        );
        this.isLoading = false;
        this.percentageDownloaded = 0;
        return;
    }
  }
}
