/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { ModalsService } from '../modals.service';
import { TranslocoModule } from '@ngneat/transloco';
import { DownloadButtonComponent } from '../../buttons/download-button/download-button.component';

@Component({
    selector: 'app-download-modal',
    templateUrl: './download-modal.component.html',
    standalone: true,
    imports: [DownloadButtonComponent, TranslocoModule],
})
export class DownloadModalComponent implements OnInit {
  public modalActive!: boolean;
  public modalFileId!: string;
  public modalFileName!: string;
  public modalHasPassword!: boolean;

  constructor(private modalService: ModalsService) {}

  ngOnInit() {
    this.modalActive = false;
    this.modalService.activateDownloadModal$.subscribe(
      (nextModalActiveValue) => {
        this.modalActive = nextModalActiveValue.modalActive;
        this.modalFileId = nextModalActiveValue.modalFileId;
        this.modalFileName = nextModalActiveValue.modalFileName;
        this.modalHasPassword = nextModalActiveValue.modalFileHasPassword;
      }
    );
  }

  public closeModal() {
    this.modalService.deactivateDownloadModal();
  }
}
