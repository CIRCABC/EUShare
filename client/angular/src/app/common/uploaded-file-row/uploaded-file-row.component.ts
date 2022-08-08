/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, Input, OnInit } from '@angular/core';
import { faFile, faLock } from '@fortawesome/free-solid-svg-icons';
import { FileInfoUploader } from '../../openapi';
import { ModalsService } from '../modals/modals.service';
import { DownloadsService } from '../../services/downloads.service';
import { UploadedFilesService } from '../../services/uploaded-files.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-uploaded-file-row',
  templateUrl: './uploaded-file-row.component.html',
  styleUrls: ['./uploaded-file-row.component.scss'],
})
export class UploadedFileRowComponent implements OnInit {
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

  public isAdminPage = false;

  constructor(
    private modalService: ModalsService,
    private downloadsService: DownloadsService,
    private uploadService: UploadedFilesService,
    private router: Router
  ) {}

  ngOnInit() {
    if (this.router.url.indexOf('/administration/')!=-1) {
      this.isAdminPage = true;
    }
  }

  public async tryDownload() {
    if (this.file.hasPassword) {
      this.modalService.activateDownloadModal(
        this.file.fileId,
        this.file.name,
        this.file.hasPassword
      );
    } else {
      await this.downloadsService.download(this.file.fileId, this.file.name);
    }
  }

  public openAddRecipientsModal() {
    this.modalService.activateAddRecipientsModal(
      this.file.name,
      this.file.fileId
    );
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
