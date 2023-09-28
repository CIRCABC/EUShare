/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { AbuseReport } from '../../../openapi/model/abuseReport';
import { TranslocoModule } from '@ngneat/transloco';
import { SessionStorageService } from '../../../services/session-storage.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AbuseService } from '../../../openapi/api/abuse.service';
import { NotificationService } from '../../notification/notification.service';

@Component({
  selector: 'app-abuse-dialog',
  templateUrl: './abuse-dialog.component.html',
  standalone: true,
  imports: [MatDialogModule, FormsModule, CommonModule, TranslocoModule],
})
export class AbuseDialogComponent {
  abuseReport: AbuseReport = {
    reporter: '',
    fileId: '',
    reason: '',
    description: '',
    date: '2000-01-01',
    status: false,
  };

  abuseReasons = [
    'abuse_reason_inappropriate_content',
    'abuse_reason_hate_violence_threats',
    'abuse_reason_malware_suspected',
    'abuse_reason_phishing_attempt',
    'abuse_reason_copyright_infringement',
    'abuse_reason_advertising_propaganda',
    'abuse_reason_spam',
    'abuse_reason_other_issues',
  ];

  authentified: boolean = false;
  constructor(
    public dialogRef: MatDialogRef<AbuseDialogComponent>,
    private sessionApi: SessionStorageService,
    private abuseService: AbuseService,
    private notificationService: NotificationService,
    @Inject(MAT_DIALOG_DATA) public fileId: string,
  ) {
    const userInfo = this.sessionApi.getStoredUserInfo();
    this.abuseReport.reporter = userInfo?.email;
    this.abuseReport.reason = this.abuseReasons[0];
    this.abuseReport.fileId = this.fileId;

    if (userInfo) this.authentified = true;
  }

  submitForm(): void {
    this.abuseService.createAbuseReport(this.abuseReport).subscribe(() => {
      this.notificationService.addSuccessMessageTranslation(
        'abuse.feedback',
        undefined,
        true,
      );
      this.dialogRef.close(this.abuseReport);
    });
  }

  cancel() {
    this.dialogRef.close();
  }
}
