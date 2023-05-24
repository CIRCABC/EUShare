/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ModalsService } from '../common/modals/modals.service';
import { FileService } from '../openapi/api/file.service';
import { firstValueFrom, map } from 'rxjs';
import { TranslocoModule } from '@ngneat/transloco';
import { DownloadButtonComponent } from '../common/buttons/download-button/download-button.component';
import { NgIf, SlicePipe } from '@angular/common';

@Component({
  selector: 'app-filelink',
  templateUrl: './filelink.component.html',
  styleUrls: ['./filelink.component.scss'],
  standalone: true,
  imports: [NgIf, DownloadButtonComponent, TranslocoModule, SlicePipe],
})
export class FilelinkComponent implements OnInit {
  public fileName!: string;
  public isFilePasswordProtected!: boolean;
  public fileId!: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private modalService: ModalsService,
    private fileApi: FileService
  ) {}

  download() {
    this.modalService.activateDownloadModal(
      this.fileId,
      this.fileName,
      this.isFilePasswordProtected
    );
  }

  async ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      await firstValueFrom(
        this.fileApi.getFileInfo(id).pipe(
          map((fileinfo) => {
            this.fileId = id;
            this.fileName = fileinfo.name;
            this.isFilePasswordProtected = fileinfo.hasPassword;
          })
        )
      );
    } else {
      this.router.navigateByUrl('/home');
    }
  }
}
