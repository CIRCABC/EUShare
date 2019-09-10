/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit, Input } from '@angular/core';
import { FileInfoRecipient, UsersService } from '../../openapi';
import { NotificationService } from '../notification/notification.service';

@Component({
  selector: 'app-download-file-row-container',
  templateUrl: './download-file-row-container.component.html'
})
export class DownloadFileRowContainerComponent implements OnInit {
  // tslint:disable-next-line:no-input-rename
  @Input('userId')
  private userId!: string;

  private pageSize = 10;
  private pageNumber = 0;

  public fileInfoRecipientArray!: FileInfoRecipient[];
  private fileInfoRecipientArrayPrevious!: FileInfoRecipient[];
  private fileInfoRecipientArrayNext!: FileInfoRecipient[];

  constructor(
    private userService: UsersService,
    private notificationService: NotificationService
  ) {}

  async ngOnInit() {
    if (this.userId) {
      this.fileInfoRecipientArray = await this.userService
        .getFilesFileInfoRecipient(this.userId, this.pageSize, this.pageNumber)
        .toPromise();
    } else {
      this.notificationService.addErrorMessage(
        'A problem occured while downloading files information. Please contact the support.'
      );
    }
  }
}
