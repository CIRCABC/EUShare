/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { TranslocoModule } from '@ngneat/transloco';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TrustRequest } from '../../../openapi/model/trustRequest';
import { TrustService } from '../../../openapi/api/trust.service';
import { NotificationService } from '../../notification/notification.service';

@Component({
  selector: 'app-upload-rights-dialog',
  templateUrl: './upload-rights-dialog.component.html',
  styleUrls: ['./upload-rights-dialog.component.scss'],
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatCheckboxModule,
    TranslocoModule,
    FormsModule,
    CommonModule,
  ],
})
export class UploadRightsDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<UploadRightsDialogComponent>,
    private trustService: TrustService,
    private notificationService: NotificationService
  ) {}

  editorContent: string = '';
  terms: boolean = false;

  isValid(): boolean {
    return this.editorContent.length != 0 && this.terms;
  }

  closeDialog() {
    this.dialogRef.close();
  }

  submit() {
    const trustRequest: TrustRequest = {
      description: this.editorContent,
    };

    this.trustService.sendTrustRequest(trustRequest).subscribe(() => {
      this.notificationService.addSuccessMessageTranslation(
        'no.upload.rights.requestsuccess'
      );
      this.dialogRef.close({ description: this.editorContent });
    });
  }
  cancel() {
    this.dialogRef.close();
  }
}
