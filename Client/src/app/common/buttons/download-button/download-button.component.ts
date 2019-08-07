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

@Component({
  selector: 'app-download-button',
  templateUrl: './download-button.component.html',
  styleUrls: ['./download-button.component.css']
})
export class DownloadButtonComponent implements OnInit {

  @Input('fileId')
  fileId!: string;

  @Input('fileName')
  fileName!: string;

  @Input('filePassword')
  filePassword?: string;

  @Input('buttonIsOutlined')
  buttonIsOutlined?: boolean = false;

  @Input('buttonIsLarge')
  buttonIsLarge?: boolean = false;

  @Input('buttonIsFullwidth')
  buttonIsFullwidth?: boolean = false;

  @Input('isShowFileName')
  isShowFileName?: boolean = false;

  public isLoading: boolean = false;

  constructor(private notificationService: NotificationService, private fileApi: FileService) { }

  ngOnInit() {
  }

  public download() {
    this.isLoading = true;
    this.fileApi.getFile(this.fileId, this.filePassword).toPromise().then(file => {
      saveAs(file, this.fileName);
      this.isLoading = false;
    }).catch(error => {
      this.notificationService.errorMessageToDisplay(error, 'downloading your file');
      this.isLoading = false;
    });
  }

}
