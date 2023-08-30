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
  AbuseService,
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
    private abuseService: AbuseService,
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
    
  }

  async validate() {
    await this.updateAbuseReportStatus(true);
  }

  async reject() {
    await this.updateAbuseReportStatus(false);
  }

  private async updateAbuseReportStatus(status: boolean) {
    this.abuseReportDetails.status = status;
    await this.abuseService.updateAbuseReport(this.abuseReportDetails.ID as string, this.convertToAbuseReport(this.abuseReportDetails)).toPromise();
    await this.dialogRef.close();
  }

  cancel() {
    this.dialogRef.close();
  }


   convertToAbuseReport(details: AbuseReportDetails): AbuseReport {
    const abuseReport: AbuseReport = {
        ID: details.ID,
        reporter: details.reporter,
        fileId: details.fileId,
        reason: details.reason,
        description: details.description,
        date: details.date,
        status: details.status,
    };
    return abuseReport;
}
}
