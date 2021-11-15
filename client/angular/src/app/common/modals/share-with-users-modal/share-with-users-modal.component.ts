/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { ModalsService } from '../modals.service';
import { Recipient, FileService } from '../../../openapi';
import { NotificationService } from '../../notification/notification.service';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-share-with-users-modal',
  templateUrl: './share-with-users-modal.component.html',
  styleUrls: ['./share-with-users-modal.component.scss'],
  preserveWhitespaces: true,
})
export class ShareWithUsersModalComponent implements OnInit {
  public modalActive = false;
  public modalFileName = '';
  private modalFileId = '';
  public recipients: Recipient[] = [];
  private modalFileIsPasswordProtected = false;

  constructor(
    private modalService: ModalsService,
    private fileApi: FileService,
    private notificationService: NotificationService
  ) {}

  public closeModal() {
    this.modalService.deactivateShareWithUsersModal();
  }

  ngOnInit() {
    this.modalActive = false;
    this.modalService.activateShareWithUsersModal$.subscribe(
      (nextModalActiveValue) => {
        this.modalActive = nextModalActiveValue.modalActive;
        this.modalFileId = nextModalActiveValue.modalFileId;
        this.modalFileName = nextModalActiveValue.modalFileName;
        this.modalFileIsPasswordProtected =
          nextModalActiveValue.modalFileHasPassword;
        this.recipients = nextModalActiveValue.recipients;
      }
    );
  }

  public deleteShare(shareId: string, shareName: string, shareIndex: number) {
    firstValueFrom(
      this.fileApi.deleteFileSharedWithUser(this.modalFileId, shareId)
    )
      .then((_success) => {
        this.recipients.splice(shareIndex, 1);
        this.notificationService.addSuccessMessageTranslation(
          'successfully.removed',
          { fileName: this.modalFileName, shareName }
        );
      })
      .catch((_error) => {
        // managed in the interceptor
      });
  }

  public copyLink(i: number) {
    const selBox = document.createElement('textarea');
    selBox.style.position = 'fixed';
    selBox.style.left = '0';
    selBox.style.top = '0';
    selBox.style.opacity = '0';
    selBox.value = this.formatLink(i);
    document.body.appendChild(selBox);
    selBox.focus();
    selBox.select();
    document.execCommand('copy');
    document.body.removeChild(selBox);
    this.notificationService.addSuccessMessageTranslation(
      'copied.file.link',
      undefined,
      true
    );
  }

  public formatLink(i: number) {
    const isPasswordProtected = this.modalFileIsPasswordProtected;

    let fileLinkBuild = `${window.location.protocol}//${
      window.location.host
    }/filelink/${this.recipients[i].downloadLink}/${encodeURIComponent(
      btoa(this.modalFileName) // nosonar
    )}/`;
    fileLinkBuild = isPasswordProtected
      ? `${fileLinkBuild}1`
      : `${fileLinkBuild}0`;
    return fileLinkBuild;
  }
}
