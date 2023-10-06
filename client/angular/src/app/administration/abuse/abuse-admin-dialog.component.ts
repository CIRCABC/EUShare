/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { CommonModule, DatePipe } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { TranslocoModule } from '@ngneat/transloco';
import { AbuseReport, AbuseReportDetails, UserInfo } from '../../openapi';

export const StatusEnumLabels = {
  REGULAR: "Delete file. Keep all user's rights.",
  SUSPENDED: 'Delete file. Prevent user from uploading.',
};

type StatusEnumKeys = (keyof typeof StatusEnumLabels)[];
@Component({
  selector: 'app-abuse-admin-dialog',
  templateUrl: './abuse-admin-dialog.component.html',
  styleUrls: ['./abuse-admin-dialog.component.scss'],
  standalone: true,
  imports: [MatDialogModule, FormsModule, TranslocoModule, CommonModule],
  providers: [DatePipe],
})
export class AbuseAdminDialogComponent {
  abuseReportDetails: AbuseReportDetails;
  statusOptions = Object.values(UserInfo.StatusEnum);
  statusLabels = StatusEnumLabels;
  statusKeys = Object.keys(StatusEnumLabels) as StatusEnumKeys;

  constructor(
    public dialogRef: MatDialogRef<AbuseAdminDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AbuseReportDetails,
  ) {
    this.abuseReportDetails = { ...data };
  }

  save() {
    this.abuseReportDetails.status = AbuseReport.StatusEnum.Approved;
    console.log(this.abuseReportDetails);
    this.dialogRef.close(this.abuseReportDetails);
  }

  cancel() {
    this.dialogRef.close();
  }
}
