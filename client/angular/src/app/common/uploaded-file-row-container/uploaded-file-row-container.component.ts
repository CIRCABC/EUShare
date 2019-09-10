/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, Input, OnInit } from '@angular/core';
import { FileInfoUploader, UsersService } from '../../openapi';
import { NotificationService } from '../notification/notification.service';

@Component({
  selector: 'app-uploaded-file-row-container',
  templateUrl: './uploaded-file-row-container.component.html'
})
export class FileRowContainerComponent implements OnInit {
  // tslint:disable-next-line:no-input-rename
  @Input('userId')
  private userId!: string;

  // tslint:disable-next-line:no-input-rename
  @Input('displayAsAdministrator')
  public displayAsAdministrator = false;

  // tslint:disable-next-line:no-input-rename
  @Input('displayAsUploader')
  public displayAsUploader = true;

  private pageSize = 10;
  private pageNumber = 0;

  public fileInfoUploaderArray!: FileInfoUploader[];
  private fileInfoUploaderArrayPrevious!: FileInfoUploader[];
  private fileInfoUploaderArrayNext!: FileInfoUploader[];

  constructor(
    private userService: UsersService,
    private notificationService: NotificationService
  ) {}

  async ngOnInit() {
    if (this.userId) {
      this.fileInfoUploaderArray = await this.userService
        .getFilesFileInfoUploader(this.userId, this.pageSize, this.pageNumber)
        .toPromise();
    } else {
      this.notificationService.addErrorMessage(
        'A problem occured while downloading files information. Please contact the support.'
      );
    }
  }

  destroyOneFile(fileId: string) {
    this.fileInfoUploaderArray = this.fileInfoUploaderArray.filter(
      file => file.fileId !== fileId
    );
  }
}
