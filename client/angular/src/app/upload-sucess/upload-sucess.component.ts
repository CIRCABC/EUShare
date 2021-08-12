/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../common/notification/notification.service';
import { FileInfoUploader } from '../openapi';
import { faUpload, faCheckCircle } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-upload-sucess',
  templateUrl: './upload-sucess.component.html',
  styleUrls: ['./upload-sucess.component.scss'],
})
export class UploadSucessComponent implements OnInit {
  public fileInfoUploader!: FileInfoUploader;

  public faCheckCircle = faCheckCircle;

  constructor(private notificationService: NotificationService) {}

  async ngOnInit() {
    const passedData = history.state.data;
    if (this.isFileInfoUploader(passedData)) {
      this.fileInfoUploader = <FileInfoUploader>passedData;
    } else {
      this.notificationService.addErrorMessage(
        'A problem occured while navigation to upload success. Please contact the support.'
      );
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
    this.notificationService.addSuccessMessage('Copied file link', true);
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
    const isPasswordProtected = this.fileInfoUploader.hasPassword;

    let fileLinkBuild =
      window.location.protocol +
      '//' +
      window.location.host +
      '/filelink/' +
      this.fileInfoUploader.sharedWith[i].downloadLink +
      '/' +
      encodeURIComponent(btoa(this.fileInfoUploader.name)) +
      '/';
    fileLinkBuild = isPasswordProtected
      ? fileLinkBuild + '1'
      : fileLinkBuild + '0';
    return fileLinkBuild;
  }
}
