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
import { CommonModule, NgIf, SlicePipe } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AbuseDialogComponent } from '../common/dialogs/abuse-dialog/abuse-dialog.component';
import { AbuseReport } from '../openapi/model/abuseReport';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-filelink',
  templateUrl: './filelink.component.html',
  styleUrls: ['./filelink.component.scss'],
  standalone: true,
  imports: [
    NgIf,
    DownloadButtonComponent,
    CommonModule,
    FormsModule,
    TranslocoModule,
    SlicePipe,
    MatDialogModule,
  ],
})
export class FilelinkComponent implements OnInit {
  public fileName!: string;
  public isFilePasswordProtected!: boolean;
  public fileId!: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private modalService: ModalsService,
    private fileApi: FileService,
    private dialog: MatDialog,
  ) {}

  download() {
    this.modalService.activateDownloadModal(
      this.fileId,
      this.fileName,
      this.isFilePasswordProtected,
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
}
