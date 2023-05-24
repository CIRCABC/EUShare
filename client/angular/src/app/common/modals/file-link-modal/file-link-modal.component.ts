/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../notification/notification.service';
import { ModalsService } from '../modals.service';

@Component({
  selector: 'app-file-link-modal',
  templateUrl: './file-link-modal.component.html',
  standalone: true,
})
export class FileLinkModalComponent implements OnInit {
  public modalActive = false;
  public fileLink!: string;
  public isLoading = false;

  constructor(
    private modalService: ModalsService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.modalService.activateFileLinkModal$.subscribe(
      (nextModalActiveValue) => {
        this.modalActive = nextModalActiveValue.modalActive;
        this.fileLink = nextModalActiveValue.fileLink;
      }
    );
  }

  copyLink(inputElement: HTMLInputElement) {
    this.isLoading = true;
    inputElement.select();
    document.execCommand('copy'); // NOSONAR
    inputElement.setSelectionRange(0, 0);
    this.notificationService.addSuccessMessageTranslation(
      'copied.file.link',
      undefined,
      true
    );
    setTimeout(() => {
      this.isLoading = false;
    }, 300);
  }

  closeModal() {
    this.modalService.deactivateFileLinkModal();
  }
}
