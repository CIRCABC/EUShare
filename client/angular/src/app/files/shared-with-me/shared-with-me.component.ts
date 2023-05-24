/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { SessionStorageService } from '../../services/session-storage.service';
import { TranslocoModule } from '@ngneat/transloco';
import { DownloadFileRowContainerComponent } from '../../common/download-file-row-container/download-file-row-container.component';

@Component({
  selector: 'app-shared-with-me',
  templateUrl: './shared-with-me.component.html',
  standalone: true,
  imports: [DownloadFileRowContainerComponent, TranslocoModule],
})
export class SharedWithMeComponent implements OnInit {
  public userId!: string;

  constructor(private session: SessionStorageService) {}

  ngOnInit() {
    const userIdOrNull = this.session.getStoredId();
    if (userIdOrNull) {
      this.userId = userIdOrNull;
    }
  }
}
