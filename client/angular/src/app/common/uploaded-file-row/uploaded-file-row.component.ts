/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { faFile, faLock } from '@fortawesome/free-solid-svg-icons';
import { FileInfoUploader, FileService, FileStatusUpdate } from '../../openapi';
import { ModalsService } from '../modals/modals.service';
import { DownloadsService } from '../../services/downloads.service';
import { Router } from '@angular/router';
import { FileSizeFormatPipe } from '../pipes/file-size-format.pipe';
import { TranslocoModule } from '@ngneat/transloco';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgIf, NgClass, SlicePipe } from '@angular/common';
import { firstValueFrom } from 'rxjs/internal/firstValueFrom';
import { NotificationService } from '../notification/notification.service';

@Component({
  selector: 'app-uploaded-file-row',
  templateUrl: './uploaded-file-row.component.html',
  styleUrls: ['./uploaded-file-row.component.scss'],
  standalone: true,
  imports: [
    NgIf,
    FontAwesomeModule,
    NgClass,
    TranslocoModule,
    SlicePipe,
    FileSizeFormatPipe,
  ],
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

  @Output() refreshParent: EventEmitter<void> = new EventEmitter<void>();

  public isMoreDisplayed = false;

  public faFile = faFile;
  public faLock = faLock;

  @Output() ok = new EventEmitter<void>();

  public isLoading = false;
  public percentageDownloaded = 0;

  constructor(
    private modalService: ModalsService,
    private downloadsService: DownloadsService,
    private notificationService: NotificationService,
    private fileService: FileService,
    private router: Router,
  ) {}

  
  public async tryDownload() {
    if (this.file.hasPassword) {
      await this.modalService.activateDownloadModal(
        this.file.fileId,
        this.file.name,
        this.file.hasPassword,
      );
    } else {
      const result = await this.downloadsService.download(
        this.file.fileId,
        this.file.name,
      );
      if (result === 'WRONG_PASSWORD') {
        this.notificationService.addErrorMessageTranslation(
          'wrong.password',
          undefined,
          true,
        );
      }
      if (result === 'TOO_MANY_DOWNLOADS') {
        this.notificationService.addErrorMessageTranslation(
          'too.many.downloads',
          undefined,
          true,
        );
      }
      if (result === 'OK') {
        this.ok.emit();
      }
    }
    
  }
  public openAddRecipientsModal() {
    this.modalService.activateAddRecipientsModal(
      this.file.name,
      this.file.fileId,
    );
  }

  public async delete() {
    this.modalService.activateDeleteConfirmModal(
      this.file.name,
      this.file.fileId,
    );
  }

  public async openExpirationDateModal() {
    this.modalService.activateChangeExpirationDateModal(
      this.file.name,
      this.file.fileId,
      this.file.expirationDate,
    );
  }

  public displayRecipients() {
    if (this.file.sharedWith.length >= 1) {
      this.modalService.activateShareWithUsersModal(
        this.file.name,
        this.file.fileId,
        this.file.sharedWith,
        this.file.hasPassword,
      );
    }
  }

  public displayDownloads() {
    if (this.file.sharedWith.length >= 1) {
      this.modalService.activateStatisticsModal(
        this.file.name,
        this.file.fileId,
        this.file.fileLogs,
      );
    }
  }

  public displayMore() {
    this.isMoreDisplayed = true;
  }

  public displayLess() {
    this.isMoreDisplayed = false;
  }

  public async freeze() {
    await firstValueFrom(
      this.fileService.updateStatus(this.file.fileId, {
        status: FileStatusUpdate.StatusEnum.Frozen,
      }),
    );
    this.refreshParent.emit();
  }

  public async unfreeze() {
    await firstValueFrom(
      this.fileService.updateStatus(this.file.fileId, {
        status: FileStatusUpdate.StatusEnum.Available,
      }),
    );
    this.refreshParent.emit();
  }
}
