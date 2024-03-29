/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { ModalsService } from '../modals.service';
import { FileService, FileLog } from '../../../openapi';
import { NotificationService } from '../../notification/notification.service';
import { TranslocoModule } from '@ngneat/transloco';
import { LowerCasePipe, SlicePipe } from '@angular/common';

@Component({
  selector: 'app-statistics-modal',
  templateUrl: './statistics-modal.component.html',
  styleUrls: ['./statistics-modal.component.scss'],
  preserveWhitespaces: true,
  standalone: true,
  imports: [TranslocoModule, LowerCasePipe, SlicePipe],
})
export class StatisticsModalComponent implements OnInit {
  public modalActive = false;
  public modalFileName = '';
  private modalFileId = '';
  public fileLogs: FileLog[] = [];

  constructor(
    private modalService: ModalsService,
    private fileApi: FileService,
    private notificationService: NotificationService,
  ) {}

  public closeModal() {
    this.modalService.deactivateStatisticsModal();
  }

  ngOnInit() {
    this.modalActive = false;
    this.modalService.activateStatisticsModal$.subscribe(
      (nextModalActiveValue) => {
        this.modalActive = nextModalActiveValue.modalActive;
        this.modalFileId = nextModalActiveValue.modalFileId;
        this.modalFileName = nextModalActiveValue.modalFileName;
        this.fileLogs = nextModalActiveValue.fileLogs;
      },
    );
  }

  public formatLink(link: string) {
    if (link === '') {
      return link;
    }
    return `${window.location.protocol}//${window.location.host}/share/fs/${link}`;
  }
}
