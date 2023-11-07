/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ModalsService } from '../common/modals/modals.service';
import { FileService } from '../openapi/api/file.service';
import { firstValueFrom, map } from 'rxjs';
import { TranslocoModule } from '@ngneat/transloco';
import { CommonModule, NgIf, SlicePipe } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AbuseDialogComponent } from '../common/dialogs/abuse-dialog/abuse-dialog.component';
import { AbuseReport } from '../openapi/model/abuseReport';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DownloadsService } from '../services/downloads.service';
import { NotificationService } from '../common/notification/notification.service';

@Component({
  selector: 'app-filelink',
  templateUrl: './filelink.component.html',
  styleUrls: ['./filelink.component.scss'],
  standalone: true,
  imports: [
    NgIf,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    TranslocoModule,
    SlicePipe,
    MatDialogModule,
  ],
})
export class FilelinkComponent implements OnInit {
  public fileName!: string;
  public isFilePasswordProtected!: boolean;
  public fileId!: string;

  public isFileHasPassword = false;
  public inputPassword = '';
  @Output() ok = new EventEmitter<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private modalService: ModalsService,
    private fileApi: FileService,
    private dialog: MatDialog,
    private downloadsService: DownloadsService,
    private notificationService: NotificationService,
  ) { }

  async ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      await firstValueFrom(
        this.fileApi.getFileInfo(id).pipe(
          map((fileinfo) => {
            this.fileId = id;
            this.fileName = fileinfo.name;
            this.isFilePasswordProtected = fileinfo.hasPassword;
          }),
        ),
      );
    } else {
      this.router.navigateByUrl('/home');
    }
  }

  openAbuseDialog(fileId: string): void {
    const dialogRef = this.dialog.open(AbuseDialogComponent, {
      data: fileId,
    });

    dialogRef.afterClosed().subscribe((result: AbuseReport) => {
      if (result) {
        // Handle the submitted abuse report
      }
    });
  }

  public async initiateDownload() {

    if (this.isFilePasswordProtected) {
      await this.modalService.activateDownloadModal(
        this.fileId,
        this.fileName,
        this.isFilePasswordProtected,
      );
    }
    else {
      const result = await this.downloadsService.download(
        this.fileId,
        this.fileName,
        this.inputPassword,
      );
      if (result === 'WRONG_PASSWORD') {
        this.notificationService.addErrorMessageTranslation(
          'wrong.password',
          undefined,
          true,
        );
      }
      if (result === 'TOO_MANY_DOWNLOADS') {
        this.notificationService.addErrorMessageTranslation(
          'too.many.downloads',
          undefined,
          true,
        );
      }
      if (result === 'OK') {
        this.ok.emit();
      }
    }

  }

}
