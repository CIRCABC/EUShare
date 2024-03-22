/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
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
import { Monitoring, MonitoringDetails, UserInfo } from '../../openapi';

export const StatusEnumLabels = {
  REGULAR: "Freeze file. Keep all user's rights.",
  SUSPENDED: 'Freeze file. Prevent user from uploading.',
};

export const StatusEnumLabelsUpload = {
  REGULAR: "Freeze files. Keep all user's rights.",
  SUSPENDED: 'Freeze files. Prevent user from uploading.',
};

export const StatusEnumLabelsUser = {
  REGULAR: 'Freeze files. Keep all users rights.',
  SUSPENDED: 'Freeze files. Prevent users from uploading.',
};

type StatusEnumKeys = (keyof typeof StatusEnumLabels)[];
@Component({
  selector: 'app-monitoring-admin-dialog',
  templateUrl: './monitoring-admin-dialog.component.html',
  styleUrls: ['./monitoring-admin-dialog.component.scss'],
  standalone: true,
  imports: [MatDialogModule, FormsModule, TranslocoModule, CommonModule],
  providers: [DatePipe],
})
export class MonitoringAdminDialogComponent {
  monitoringDetails: MonitoringDetails;
  statusOptions = Object.values(UserInfo.StatusEnum);
  statusLabels = StatusEnumLabels;
  statusLabelsUploads = StatusEnumLabelsUpload;
  statusLabelsUser = StatusEnumLabelsUser;
  statusKeys = Object.keys(StatusEnumLabels) as StatusEnumKeys;

  constructor(
    public dialogRef: MatDialogRef<MonitoringAdminDialogComponent>,
    private datePipe: DatePipe,
    @Inject(MAT_DIALOG_DATA) public data: MonitoringDetails,
  ) {
    this.monitoringDetails = { ...data };
  }

  save() {
    this.monitoringDetails.status = Monitoring.StatusEnum.Approved;
    this.dialogRef.close(this.monitoringDetails);
  }

  cancel() {
    this.dialogRef.close();
  }

  formatString(input: string | undefined): string {
    if (input) {
      const words = input.toLowerCase().split('_');
      words[0] = words[0].charAt(0).toUpperCase() + words[0].slice(1);
      words[words.length - 1] = `(by ${words[words.length - 1]})`;
      return words.join(' ');
    }
    return '';
  }
}
