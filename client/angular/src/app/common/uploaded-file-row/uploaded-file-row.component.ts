/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, Input } from '@angular/core';
import { faFile, faLock } from '@fortawesome/free-solid-svg-icons';
import { FileInfoUploader } from '../../openapi';
import { ModalsService } from '../modals/modals.service';
import { DownloadsService } from '../../services/downloads.service';
import { UploadedFilesService } from '../../services/uploaded-files.service';

@Component({
  selector: 'app-uploaded-file-row',
  templateUrl: './uploaded-file-row.component.html',
})
export class UploadedFileRowComponent {
  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('fileToDisplay')
  public file!: FileInfoUploader;

  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('displayAsAdministrator')
  public displayAsAdministrator = false;

  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('displayAsUploader')
  public displayAsUploader = false;

  public isMoreDisplayed = false;

  public faFile = faFile;
  public faLock = faLock;

  public isLoading = false;
  public percentageDownloaded = 0;

  constructor(
    private modalService: ModalsService,
    private downloadsService: DownloadsService,
    private uploadService: UploadedFilesService
  ) { }

  public tryDownload() {
    if (this.file.hasPassword) {
      this.modalService.activateDownloadModal(
        this.file.fileId,
        this.file.name,
        this.file.hasPassword
      );
    } else {
      this.isLoading = true;
      this.downloadsService.downloadAFile(this.file.fileId, this.file.name).subscribe({
        next: (next) => {
          this.percentageDownloaded = next.percentage;
          if (next.percentage === 100) {
            this.isLoading = false;
            this.uploadService.update();
          }
        },
        error: (_error) => {
          console.log(_error);
          this.isLoading = false;
        },
      });
      
    }
  }

  public openAddRecipientsModal() {
    this.modalService.activateAddRecipientsModal(
      this.file.name,
      this.file.fileId);
  }

  public async delete() {
    this.modalService.activateDeleteConfirmModal(
      this.file.name,
      this.file.fileId
    );
  }

  public async openExpirationDateModal() {
    this.modalService.activateChangeExpirationDateModal(
      this.file.name,
      this.file.fileId,
      this.file.expirationDate
    );
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

  public displayDownloads() {
    if (this.file.sharedWith.length >= 1) {
      this.modalService.activateStatisticsModal(
        this.file.name,
        this.file.fileId,
        this.file.fileLogs
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
