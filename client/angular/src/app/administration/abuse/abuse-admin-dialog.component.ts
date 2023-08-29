/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { TranslocoModule } from '@ngneat/transloco';
import {
  AbuseReport,
  AbuseReportDetails,
  FileService,
  UserInfo,
  UsersService,
} from '../../openapi';
import { DatePipe } from '@angular/common';
import { firstValueFrom } from 'rxjs/internal/firstValueFrom';
import { map } from 'rxjs';

@Component({
  selector: 'app-abuse-admin-dialog',
  templateUrl: './abuse-admin-dialog.component.html',
  styleUrls: ['./abuse-admin-dialog.component.scss'],
  standalone: true,
  imports: [MatDialogModule, FormsModule, TranslocoModule],
  providers: [DatePipe],
})
export class AbuseAdminDialogComponent {
  abuseReportDetails: AbuseReportDetails;

  constructor(
    public dialogRef: MatDialogRef<AbuseAdminDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AbuseReport,
    private datePipe: DatePipe,
    private fileApi: FileService,
  ) {
    this.abuseReportDetails = {
      ID: data.ID,
      reporter: data.reporter,
      fileId: data.fileId,
      reason: data.reason,
      description: data.description,
      date: data.date,
      status: data.status,
      filename: '',
      filesize: 0,
      uploader_email: '',
      uploader_name: '',
      uploader_status: 'ban',
    };
  }

  async ngOnInit() {
    if (this.data.fileId) {
      await firstValueFrom(
        this.fileApi.getFileInfo(this.data.fileId).pipe(
          map((fileinfo) => {
            this.abuseReportDetails.filename = fileinfo.name;
            this.abuseReportDetails.filesize = fileinfo.size;
          }),
        ),
      );
    }
  }

  submitApprove(formData: any): void {
    formData.form.value.action = true;
    this.dialogRef.close(formData.form.value);
  }
  submitDeny(formData: any): void {
    formData.form.value.action = false;
    this.dialogRef.close(formData.form.value);
  }

  cancel() {
    this.dialogRef.close();
  }
}
