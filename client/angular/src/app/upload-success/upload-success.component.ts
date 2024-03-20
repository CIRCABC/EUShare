/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { Clipboard } from '@angular/cdk/clipboard';
import { NotificationService } from '../common/notification/notification.service';
import { FileInfoUploader } from '../openapi';
import { faCheckCircle } from '@fortawesome/free-solid-svg-icons';
import { I18nService } from '../common/i18n/i18n.service';
import { environment } from '../../environments/environment';
import { TranslocoModule } from '@ngneat/transloco';
import { CommonModule, LowerCasePipe, SlicePipe } from '@angular/common';
import { QRCodeModule } from 'angularx-qrcode';

@Component({
  selector: 'app-upload-success',
  templateUrl: './upload-success.component.html',
  styleUrls: ['./upload-success.component.scss'],
  standalone: true,
  imports: [TranslocoModule, QRCodeModule, LowerCasePipe, SlicePipe, QRCodeModule, CommonModule],
})
export class UploadSuccessComponent implements OnInit {
  public fileInfoUploader!: FileInfoUploader;
  public faCheckCircle = faCheckCircle;
  private frontend_url = '';


  public qrCodeModalActive = false;
  public qrCodeData = '';

  constructor(
    private notificationService: NotificationService,
    private i18nService: I18nService,
    private clipboard: Clipboard
  ) {}

  async ngOnInit() {
    this.frontend_url = environment.frontend_url;
    const passedData = history.state.data;
    if (this.isFileInfoUploader(passedData)) {
      this.fileInfoUploader = <FileInfoUploader>passedData;
    } else {
      const message = `${this.i18nService.translate(
        'problem.occurred.navigation'
      )} ${this.i18nService.contactSupport()}`;

      this.notificationService.addErrorMessage(message);
    }
  }

  public copyLink(i: number) {
    const value = this.formatLink(i);
    const success = this.clipboard.copy(value);
    if (success) {
      this.notificationService.addSuccessMessageTranslation(
        'copied.file.link',
        undefined,
        true
      );
    }
  }

  isFileInfoUploader(object: any): boolean {
    return (
      'expirationDate' in object &&
      'hasPassword' in object &&
      'name' in object &&
      'size' in object &&
      'fileId' in object &&
      'sharedWith' in object
    );
  }


  public formatLink(i: number) {
    return `${window.location.protocol}//${window.location.host}${this.frontend_url}/fs/${this.fileInfoUploader.sharedWith[i].shortUrl}`;
  }


  handleSaveAs(fileUrl: string) {
    console.log(`Saving ${fileUrl}`);
  }

  public showQRCode(i: number) {
    this.qrCodeData = this.formatLink(i);
    this.qrCodeModalActive = true;
  }

}
