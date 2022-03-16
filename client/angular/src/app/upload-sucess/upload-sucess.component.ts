/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../common/notification/notification.service';
import { FileInfoUploader } from '../openapi';
import { faCheckCircle } from '@fortawesome/free-solid-svg-icons';
import { I18nService } from '../common/i18n/i18n.service';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-upload-sucess',
  templateUrl: './upload-sucess.component.html',
  styleUrls: ['./upload-sucess.component.scss'],
})
export class UploadSucessComponent implements OnInit {
  public fileInfoUploader!: FileInfoUploader;

  public faCheckCircle = faCheckCircle;
  private frontend_url = '';

  constructor(
    private notificationService: NotificationService,
    private i18nService: I18nService
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
    const selBox = document.createElement('textarea');
    selBox.style.position = 'fixed';
    selBox.style.left = '0';
    selBox.style.top = '0';
    selBox.style.opacity = '0';
    selBox.value = this.formatLink(i);
    document.body.appendChild(selBox);
    selBox.focus();
    selBox.select();
    document.execCommand('copy');
    document.body.removeChild(selBox);
    this.notificationService.addSuccessMessageTranslation(
      'copied.file.link',
      undefined,
      true
    );
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
    const fileLinkBuild = `${window.location.protocol}//${window.location.host}${this.frontend_url}/fs/${this.fileInfoUploader.sharedWith[i].shortUrl}`;
    return fileLinkBuild;
  }
}
