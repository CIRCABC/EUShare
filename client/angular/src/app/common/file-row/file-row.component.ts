/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { faFile, faEllipsisH, faLock } from '@fortawesome/free-solid-svg-icons';
import { FileInfoUploader, FileService } from '../../openapi';
import { ModalsService } from '../modals/modals.service';
import { NotificationService } from '../notification/notification.service';

@Component({
  selector: 'app-file-row',
  templateUrl: './file-row.component.html',
  styleUrls: ['./file-row.component.scss']
})
export class FileRowComponent {
  // tslint:disable-next-line:no-input-rename
  @Input('fileToDisplay')
  public file!: FileInfoUploader;

  // tslint:disable-next-line:no-input-rename
  @Input('displayAddRecipientsButton')
  public displayAddRecipients = false;

  // tslint:disable-next-line:no-output-rename
  @Output('destroyRow')
  public destroyRow: EventEmitter<string> = new EventEmitter<string>();

  public isMoreDisplayed = false;

  public faFile = faFile;
  public faEllipsisH = faEllipsisH;
  public faLock = faLock;

  constructor(
    private modalService: ModalsService,
    private fileApi: FileService,
    private notificationService: NotificationService
  ) {}

  public openDownloadModal(
    fileId: string,
    fileName: string,
    fileHasPassword: boolean
  ) {
    this.modalService.activateDownloadModal(fileId, fileName, fileHasPassword);
  }

  public openAddRecipientsModal(fileName: string, fileId: string) {
    this.modalService.activateAddRecipientsModal(fileName, fileId);
  }

  public delete() {
    this.fileApi
      .deleteFile(this.file.fileId)
      .toPromise()
      .then(success => {
        this.notificationService.addSuccessMessage(
          'Successfully deleted file named ' + this.file.name
        );
        this.destroyRow.next(this.file.fileId);
      })
      .catch(error => {
        this.notificationService.errorMessageToDisplay(
          error,
          'deleting your file'
        );
      });
  }

  public displayRecipients() {
    if (this.file.sharedWith.length >= 1) {
      this.modalService.activateShareWithUsersModal(
        this.file.name,
        this.file.fileId,
        this.file.sharedWith,
        this.file.hasPassword
      );
    }
  }

  public displayMore() {
    this.isMoreDisplayed = true;
  }

  public displayLess() {
    this.isMoreDisplayed = false;
  }
}
