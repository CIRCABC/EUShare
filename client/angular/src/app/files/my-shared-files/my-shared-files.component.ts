/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SessionStorageService } from '../../services/session-storage.service';
import { TranslocoModule } from '@ngneat/transloco';
import { FileRowContainerComponent } from '../../common/uploaded-file-row-container/uploaded-file-row-container.component';

@Component({
    selector: 'app-my-shared-files',
    templateUrl: './my-shared-files.component.html',
    standalone: true,
    imports: [FileRowContainerComponent, TranslocoModule],
})
export class MySharedFilesComponent implements OnInit {
  public myId!: string;

  constructor(private session: SessionStorageService, private router: Router) {}

  public ngOnInit() {
    const id = this.session.getStoredId();
    if (id) {
      this.myId = id;
    } else {
      this.router.navigateByUrl('');
    }
  }
}
