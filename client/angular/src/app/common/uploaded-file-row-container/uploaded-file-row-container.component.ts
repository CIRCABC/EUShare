/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { FileInfoUploader, UsersService } from '../../openapi';
import { NotificationService } from '../notification/notification.service';
import { UploadedFilesService } from '../../services/uploaded-files.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-uploaded-file-row-container',
  templateUrl: './uploaded-file-row-container.component.html',
  styleUrls: ['./uploaded-file-row-container.component.scss']
})
export class FileRowContainerComponent implements OnInit, OnDestroy {

  // tslint:disable-next-line:no-input-rename
  @Input('userId')
  private userId!: string;

  // tslint:disable-next-line:no-input-rename
  @Input('displayAsAdministrator')
  public displayAsAdministrator = false;

  // tslint:disable-next-line:no-input-rename
  @Input('displayAsUploader')
  public displayAsUploader = true;

  public hasNextPage = false;
  public hasPreviousPage = false;
  public pageNumber = 1;

  private subscription!: Subscription;


  public fileInfoUploaderArray: FileInfoUploader[] = [];

  constructor(private fileInfoUploaderService: UploadedFilesService
  ) { }

  async ngOnInit() {
    if (this.userId) {
      this.subscription = this.fileInfoUploaderService.fileInfoUploaderArrayAndMetaData$.subscribe(next => {
        this.fileInfoUploaderArray = next.fileInfoUploaderArray;
        this.hasNextPage = next.hasNextPage;
        this.hasPreviousPage = next.hasPreviousPage;
        this.pageNumber = next.pageNumber + 1;
      });
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
    this.subscription.unsubscribe();
    this.fileInfoUploaderArray = [];
  }
}
