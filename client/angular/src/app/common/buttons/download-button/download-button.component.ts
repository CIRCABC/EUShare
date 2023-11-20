/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DownloadsService } from '../../../services/downloads.service';
import { NotificationService } from '../../notification/notification.service';
import { TranslocoModule } from '@ngneat/transloco';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

@Component({
  selector: 'app-download-button',
  templateUrl: './download-button.component.html',
  styleUrls: ['./download-button.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, TranslocoModule],
})
export class DownloadButtonComponent {
  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('fileId')
  public fileId!: string;

  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('fileName')
  public fileName!: string;

  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('isFileHasPassword')
  public isFileHasPassword = false;

  @Output() ok = new EventEmitter<void>();

  public inputPassword = '';

  constructor(
    private downloadsService: DownloadsService,
    private notificationService: NotificationService
  ) {}

  public async download() {
    const result = await this.downloadsService.download(
      this.fileId,
      this.fileName,
      this.inputPassword
    );
    if (result === 'WRONG_PASSWORD') {
      this.notificationService.addErrorMessageTranslation(
        'wrong.password',
        undefined,
        true
      );
    }
    if (result === 'TOO_MANY_DOWNLOADS') {
      this.notificationService.addErrorMessageTranslation(
        'too.many.downloads',
        undefined,
        true
      );
    }
    if (result === 'OK') {
      this.ok.emit();
    }
  }
}
