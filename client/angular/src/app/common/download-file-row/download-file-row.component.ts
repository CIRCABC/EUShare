/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AbuseReport, FileInfoRecipient } from '../../openapi';
import { ModalsService } from '../modals/modals.service';
import { faFile, faLock } from '@fortawesome/free-solid-svg-icons';
import { DownloadsService } from '../../services/downloads.service';
import { FileSizeFormatPipe } from '../pipes/file-size-format.pipe';
import { TranslocoModule } from '@ngneat/transloco';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgIf, SlicePipe } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AbuseDialogComponent } from '../dialogs/abuse-dialog/abuse-dialog.component';
import { NotificationService } from '../notification/notification.service';

@Component({
  selector: 'app-download-file-row',
  templateUrl: './download-file-row.component.html',
  standalone: true,
  imports: [
    NgIf,
    FontAwesomeModule,
    TranslocoModule,
    SlicePipe,
    FileSizeFormatPipe,
    MatDialogModule,
  ],
})
export class DownloadFileRowComponent {
  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('fileToDisplay')
  fileToDisplay!: FileInfoRecipient;

  public isMoreDisplayed = false;
  public faFile = faFile;
  public faLock = faLock;
  @Output() ok = new EventEmitter<void>();
  
  constructor(
    private modalService: ModalsService,
    private downloadsService: DownloadsService,
    private notificationService: NotificationService,
    private dialog: MatDialog,
  ) {}

  public displayMore() {
    this.isMoreDisplayed = true;
  }

  public displayLess() {
    this.isMoreDisplayed = false;
  }

  public async tryDownload() {
    if (this.fileToDisplay.hasPassword) {
      await this.modalService.activateDownloadModal(
        this.fileToDisplay.fileId,
        this.fileToDisplay.name,
        this.fileToDisplay.hasPassword,
      );
    } else {
      const result = await this.downloadsService.download(
        this.fileToDisplay.fileId,
        this.fileToDisplay.name,
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

  openAbuseDialog(): void {
    const dialogRef = this.dialog.open(AbuseDialogComponent, {
      data: this.fileToDisplay.fileId,
    });

    dialogRef.afterClosed().subscribe((result: AbuseReport) => {
      if (result) {
        // Handle the submitted abuse report
      }
    });
  }
}
