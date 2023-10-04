/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { FileInfoUploader } from '../../openapi';
import { UploadedFilesService } from '../../services/uploaded-files.service';
import { Subscription } from 'rxjs';
import { TranslocoModule } from '@ngneat/transloco';
import { UploadedFileRowComponent } from '../uploaded-file-row/uploaded-file-row.component';
import { NgIf, NgFor } from '@angular/common';

@Component({
  selector: 'app-uploaded-file-row-container',
  templateUrl: './uploaded-file-row-container.component.html',
  styleUrls: ['./uploaded-file-row-container.component.scss'],
  standalone: true,
  imports: [NgIf, NgFor, UploadedFileRowComponent, TranslocoModule],
})
export class FileRowContainerComponent implements OnInit, OnDestroy {
  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('userId')
  public userId!: string;

  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('displayAsAdministrator')
  public displayAsAdministrator = false;

  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('displayAsUploader')
  public displayAsUploader = true;

  public hasNextPage = false;
  public hasPreviousPage = false;
  public pageNumber = 1;

  private subscription!: Subscription;

  public fileInfoUploaderArray: FileInfoUploader[] = [];

  constructor(private fileInfoUploaderService: UploadedFilesService) {}

  async ngOnInit() {
    if (this.userId) {
      this.subscription =
        this.fileInfoUploaderService.fileInfoUploaderArrayAndMetaData$.subscribe(
          (next) => {
            this.fileInfoUploaderArray = next.fileInfoUploaderArray;
            this.hasNextPage = next.hasNextPage;
            this.hasPreviousPage = next.hasPreviousPage;
            this.pageNumber = next.pageNumber + 1;
          },
        );
      await this.fileInfoUploaderService.reinit(this.userId);
    }
  }

  public async nextPage() {
    await this.fileInfoUploaderService.nextPage();
  }

  public async previousPage() {
    await this.fileInfoUploaderService.previousPage();
  }

  ngOnDestroy(): void {
    this.fileInfoUploaderArray = [];
    this.subscription.unsubscribe();
  }

  public async update(){
    await this.fileInfoUploaderService.reinit(this.userId);
  }

  
}
