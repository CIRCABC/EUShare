/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faLock } from '@fortawesome/free-solid-svg-icons';
import { ModalsService } from '../../common/modals/modals.service';
import { NotificationService } from '../../common/notification/notification.service';
import {
  FileInfoRecipient,
  FileService,
  SessionService,
  UsersService
} from '../../openapi';

@Component({
  selector: 'app-shared-with-me',
  templateUrl: './shared-with-me.component.html',
  styleUrls: ['./shared-with-me.component.css']
})
export class SharedWithMeComponent implements OnInit {
  public faLock = faLock;
  public fileInfoRecipientArray!: FileInfoRecipient[];
  private pageSize = 10;
  private pageNumber = 0;

  constructor(
    private session: SessionService,
    private userApi: UsersService,
    private notificationService: NotificationService,
    private modalService: ModalsService
  ) {}

  ngOnInit() {
    this.initializeFileInfoRecipientArray();
  }

  private initializeFileInfoRecipientArray() {
    const userId = this.session.getStoredId();
    if (userId) {
      this.userApi
        .getFilesFileInfoRecipient(userId, this.pageSize, this.pageNumber)
        .toPromise()
        .then(fileInfoRecipientArray => {
          this.fileInfoRecipientArray = fileInfoRecipientArray;
        })
        .catch(error => {
          this.notificationService.errorMessageToDisplay(
            error,
            'fetching files shared with you'
          );
        });
    }
  }

  public openDownloadModal(fileId: string, fileName: string, fileHasPassword: boolean) {
    this.modalService.activateDownloadModal(fileId, fileName, fileHasPassword);
  }

}
