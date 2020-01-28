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
  templateUrl: './download-file-row-container.component.html',
  styleUrls: ['./download-file-row-container.component.scss']
})
export class DownloadFileRowContainerComponent implements OnInit {
  // tslint:disable-next-line:no-input-rename
  @Input('userId')
  private userId!: string;

  private pageSize = 10;
  public pageNumber = 0;

  public fileInfoRecipientArray: FileInfoRecipient[] = [];
  public fileInfoRecipientArrayPrevious: FileInfoRecipient[] = [];
  public fileInfoRecipientArrayNext: FileInfoRecipient[] = [];

  constructor(
    private userService: UsersService,
    private notificationService: NotificationService
  ) {}

  async ngOnInit() {
    if (this.userId) {
      this.pageNumber = 0;
      await this.update();
    }
  }

  public async nextPage() {
    this.pageNumber = this.pageNumber + 1;
    this.fileInfoRecipientArrayPrevious = this.fileInfoRecipientArray;
    this.fileInfoRecipientArray = this.fileInfoRecipientArrayNext;
    this.fileInfoRecipientArrayNext = [];

    if (this.fileInfoRecipientArray.length === this.pageSize) {
      try {
        await this.getNextFileInfoUploader();
      } catch (error) {
        this.notificationService.addErrorMessage(
          'A problem occured while downloading files information. Please contact the support.'
        );
      }
    }
  }

  public async previousPage() {
    this.pageNumber = this.pageNumber - 1;
    this.fileInfoRecipientArrayNext = this.fileInfoRecipientArray;
    this.fileInfoRecipientArray = this.fileInfoRecipientArrayPrevious;
    this.fileInfoRecipientArrayPrevious = [];
    if (this.pageNumber > 0) {
      try {
        await this.getPreviousFileInfoUploader();
      } catch (error) {
        this.notificationService.addErrorMessage(
          'A problem occured while downloading files information. Please contact the support.'
        );
      }
    }
  }

  private async getCurrentFileInfoUploader() {
    this.fileInfoRecipientArray = await this.userService
      .getFilesFileInfoRecipient(this.userId, this.pageSize, this.pageNumber)
      .toPromise();
  }

  private async getNextFileInfoUploader() {
    this.fileInfoRecipientArrayNext = await this.userService
      .getFilesFileInfoRecipient(
        this.userId,
        this.pageSize,
        this.pageNumber + 1
      )
      .toPromise();
  }

  private async getPreviousFileInfoUploader() {
    this.fileInfoRecipientArrayPrevious = await this.userService
      .getFilesFileInfoRecipient(
        this.userId,
        this.pageSize,
        this.pageNumber - 1
      )
      .toPromise();
  }

  public async update() {
    try {
      await this.getCurrentFileInfoUploader();
      if (this.fileInfoRecipientArray.length === this.pageSize) {
        await this.getNextFileInfoUploader();
      }

      if (this.pageNumber > 0) {
        await this.getPreviousFileInfoUploader();
      }
    } catch (error) {
      this.notificationService.addErrorMessage(
        'A problem occured while downloading files information. Please contact the support.'
      );
    }
  }
}
