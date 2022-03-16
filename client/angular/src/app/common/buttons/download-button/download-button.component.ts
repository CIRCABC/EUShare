/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, Input } from '@angular/core';
import { DownloadsService } from '../../../services/downloads.service';

@Component({
  selector: 'app-download-button',
  templateUrl: './download-button.component.html',
  styleUrls: ['./download-button.component.scss'],
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

  public isLoading = false;
  public percentageDownloaded = 0;
  public inputPassword = '';

  constructor(private downloadsService: DownloadsService) {}

  public download() {
    this.isLoading = true;

    this.downloadsService
      .downloadAFile(this.fileId, this.fileName, this.inputPassword)
      .subscribe({
        next: (next) => {
          this.percentageDownloaded = next.percentage;
          if (next.percentage === 100) {
            this.isLoading = false;
          }
        },
        error: (_error) => {
          this.isLoading = false;
        },
      });
  }
}
