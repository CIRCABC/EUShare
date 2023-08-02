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
import { TrustRequest } from '../../../openapi';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-trust-dialog',
  templateUrl: './trust-dialog.component.html',
  styleUrls: ['./trust-dialog.component.scss'],
  standalone: true,
  imports: [MatDialogModule, FormsModule, TranslocoModule],
  providers: [DatePipe],
})
export class TrustDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<TrustDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TrustRequest,
    private datePipe: DatePipe
  ) {}

  denyReason = '';
  action = false;

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
