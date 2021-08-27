/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import {
  FileInfoUploader,
  UsersService,
  FileService,
  Recipient,
} from '../openapi';
import { NotificationService } from '../common/notification/notification.service';
import { ModalsService } from '../common/modals/modals.service';

@Injectable({
  providedIn: 'root',
})
export class UploadedFilesService {
  private fileInfoUploaderArrayAndMetaDataSubject: Subject<FileUploaderArrayAndMetaData> =
    new Subject<FileUploaderArrayAndMetaData>();
  public fileInfoUploaderArrayAndMetaData$: Observable<FileUploaderArrayAndMetaData> =
    this.fileInfoUploaderArrayAndMetaDataSubject.asObservable();

  private fileInfoUploader: FileInfoUploader[] = [];

  private previousPageFileInfoUploader: FileInfoUploader[] = [];
  private nextPageFileInfoUploader: FileInfoUploader[] = [];

  private pageSize = 10;
  private pageNumber = 0;

  private userId!: string;

  constructor(
    private userService: UsersService,
    private notificationService: NotificationService,
    private fileService: FileService,
    private modalService: ModalsService
  ) {}

  private emitValueToObservable() {
    const fileInfoUploaderArrayAndMetaData: FileUploaderArrayAndMetaData = {
      fileInfoUploaderArray: this.fileInfoUploader,
      hasNextPage: this.nextPageFileInfoUploader.length > 0,
      hasPreviousPage: this.pageNumber > 0,
      pageNumber: this.pageNumber,
    };
    this.fileInfoUploaderArrayAndMetaDataSubject.next(
      fileInfoUploaderArrayAndMetaData
    );
  }

  public async reinit(userId: string) {
    this.pageNumber = 0;
    this.userId = userId;
    await this.update();
  }

  public async update() {
    try {
      await this.getCurrentFileInfoUploader();
      if (this.fileInfoUploader.length === this.pageSize) {
        await this.getNextFileInfoUploader();
      }

      if (this.pageNumber > 0) {
        await this.getPreviousFileInfoUploader();
      }
      this.emitValueToObservable();
    } catch (error) {
      // notification sent in interceptor
    }
  }

  public async nextPage() {
    this.pageNumber = this.pageNumber + 1;
    this.previousPageFileInfoUploader = this.fileInfoUploader;
    this.fileInfoUploader = this.nextPageFileInfoUploader;
    this.nextPageFileInfoUploader = [];

    if (this.fileInfoUploader.length === this.pageSize) {
      try {
        await this.getNextFileInfoUploader();
      } catch (error) {
        // notification sent in file interceptor
      }
    }
    this.emitValueToObservable();
  }

  public async previousPage() {
    this.pageNumber = this.pageNumber - 1;
    this.nextPageFileInfoUploader = this.fileInfoUploader;
    this.fileInfoUploader = this.previousPageFileInfoUploader;
    this.previousPageFileInfoUploader = [];
    if (this.pageNumber > 0) {
      try {
        await this.getPreviousFileInfoUploader();
      } catch (error) {
        // notification sent in interceptor
      }
    }
    this.emitValueToObservable();
  }

  private async getCurrentFileInfoUploader() {
    this.fileInfoUploader = await this.userService
      .getFilesFileInfoUploader(this.userId, this.pageSize, this.pageNumber)
      .toPromise();
  }

  private async getNextFileInfoUploader() {
    this.nextPageFileInfoUploader = await this.userService
      .getFilesFileInfoUploader(this.userId, this.pageSize, this.pageNumber + 1)
      .toPromise();
  }

  private async getPreviousFileInfoUploader() {
    this.previousPageFileInfoUploader = await this.userService
      .getFilesFileInfoUploader(this.userId, this.pageSize, this.pageNumber - 1)
      .toPromise();
  }

  public async removeOneFile(fileId: string, fileName: string) {
    try {
      await this.fileService.deleteFile(fileId).toPromise();
      this.notificationService.addSuccessMessageTranslation(
        'successfully.deleted',
        { fileName }
      );
    } catch (error) {
      // error managed in error interceptor
    }

    this.fileInfoUploader = this.fileInfoUploader.filter(
      (file) => file.fileId !== fileId
    );
    const elementToAddOrNull = this.nextPageFileInfoUploader.shift();
    if (elementToAddOrNull) {
      this.fileInfoUploader.push(elementToAddOrNull);
    }
    this.emitValueToObservable();
  }

  public async addOneRecipient(
    fileName: string,
    fileId: string,
    recipient: Recipient
  ) {
    try {
      const recipientWithLink = await this.fileService
        .postFileSharedWith(fileId, recipient)
        .toPromise();

      this.notificationService.addSuccessMessageTranslation(
        'successfully.added',
        { fileName }
      );

      this.fileInfoUploader.forEach((file) => {
        if (file.fileId === fileId) {
          file.sharedWith.push(recipientWithLink);
        }
      });

      this.emitValueToObservable();
      this.modalService.deactivateAddRecipientsModal();
    } catch (error) {
      // notification sent in error interceptor
    }
  }
}

interface FileUploaderArrayAndMetaData {
  fileInfoUploaderArray: FileInfoUploader[];
  hasNextPage: boolean;
  hasPreviousPage: boolean;
  pageNumber: number;
}
