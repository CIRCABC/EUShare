/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { ModalsService } from '../modals.service';
import { Recipient, FileService } from '../../../openapi';
import { NotificationService } from '../../notification/notification.service';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { TranslocoModule } from '@ngneat/transloco';
import { NgFor, NgIf, LowerCasePipe, SlicePipe } from '@angular/common';

@Component({
  selector: 'app-share-with-users-modal',
  templateUrl: './share-with-users-modal.component.html',
  styleUrls: ['./share-with-users-modal.component.scss'],
  preserveWhitespaces: true,
  standalone: true,
  imports: [NgFor, NgIf, TranslocoModule, LowerCasePipe, SlicePipe],
})
export class ShareWithUsersModalComponent implements OnInit {
  public modalActive = false;
  public modalFileName = '';
  private modalFileId = '';
  public recipients: Recipient[] = [];
  private frontend_url = '';

  constructor(
    private modalService: ModalsService,
    private fileApi: FileService,
    private notificationService: NotificationService
  ) {}

  public closeModal() {
    this.modalService.deactivateShareWithUsersModal();
  }

  ngOnInit() {
    this.frontend_url = environment.frontend_url;
    this.modalActive = false;
    this.modalService.activateShareWithUsersModal$.subscribe(
      (nextModalActiveValue) => {
        this.modalActive = nextModalActiveValue.modalActive;
        this.modalFileId = nextModalActiveValue.modalFileId;
        this.modalFileName = nextModalActiveValue.modalFileName;
        this.recipients = nextModalActiveValue.recipients;
      }
    );
  }

  public deleteShare(shareEmail: string | undefined, shareIndex: number) {
    if (shareEmail === undefined) {
      return;
    }
    firstValueFrom(
      this.fileApi.deleteFileSharedWithUser(this.modalFileId, shareEmail)
    )
      .then((_success) => {
        this.recipients.splice(shareIndex, 1);
        this.notificationService.addSuccessMessageTranslation(
          'successfully.removed',
          { fileName: this.modalFileName, shareName: shareEmail }
        );
      })
      .catch((_error) => {
        // managed in the interceptor
      });
  }

  public modifyDownloadNotification(recipient: Recipient, checked: boolean) {
    if (recipient.email === undefined) {
      return;
    }
    recipient.downloadNotification = checked;

    firstValueFrom(
      this.fileApi.postFileSharedWithDownloadNotification(
        this.modalFileId,
        recipient.email,
        checked
      )
    )
      .then((_success) => {
        this.notificationService.addSuccessMessageTranslation(
          checked
            ? 'successfully.set.download.notification'
            : 'successfully.unset.download.notification',
          { fileName: this.modalFileName, shareName: recipient.email }
        );
      })
      .catch((_error) => {
        // managed in the interceptor
      });
  }

  public reminderShare(shareEmail: string | undefined) {
    if (shareEmail === undefined) {
      return;
    }
    firstValueFrom(
      this.fileApi.postFileSharedWithReminder(this.modalFileId, shareEmail)
    )
      .then((_success) => {
        this.notificationService.addSuccessMessageTranslation(
          'successfully.reminded',
          { shareEmail, fileName: this.modalFileName }
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
    document.execCommand('copy'); // NOSONAR
    document.body.removeChild(selBox);
    this.notificationService.addSuccessMessageTranslation(
      'copied.file.link',
      undefined,
      true
    );
  }

  public formatLink(i: number) {
    return `${window.location.protocol}//${window.location.host}${this.frontend_url}/fs/${this.recipients[i].shortUrl}`;
  }
}
