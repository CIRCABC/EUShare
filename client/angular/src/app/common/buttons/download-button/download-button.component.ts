/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, Input, OnInit } from '@angular/core';
import { DownloadsService } from '../../../services/downloads.service';

@Component({
  selector: 'app-download-button',
  templateUrl: './download-button.component.html',
  styleUrls: ['./download-button.component.scss'],
})
export class DownloadButtonComponent implements OnInit {
  // tslint:disable-next-line:no-input-rename
  @Input('fileId')
  private fileId!: string;

  // tslint:disable-next-line:no-input-rename
  @Input('fileName')
  public fileName!: string;

  // tslint:disable-next-line:no-input-rename
  @Input('isFileHasPassword')
  public isFileHasPassword = false;

  // tslint:disable-next-line:no-input-rename
  @Input('isShowProgress')
  public isShowProgress = false;

  public isLoading = false;
  public percentageDownloaded = 0;
  public inputPassword = '';

  constructor(private downloadsService: DownloadsService) {}

  ngOnInit() {}

  public download() {
    this.isLoading = true;

    if (!this.isShowProgress) {
      this.downloadsService.displayDownloadsBox();
      this.downloadsService.downloadAFile(
        this.fileId,
        this.fileName,
        this.inputPassword,
        false
      );
    } else {
      this.downloadsService
        .downloadAFile(this.fileId, this.fileName, this.inputPassword, true)
        .subscribe(
          (next) => {
            console.log(next.percentage);
            this.percentageDownloaded = next.percentage;
            if (next.percentage === 100) {
              this.isLoading = false;
            }
          },
          (_error) => {
            this.isLoading = false;
          }
        );
    }
  }
}
