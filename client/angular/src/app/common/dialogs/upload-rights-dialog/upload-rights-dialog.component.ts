import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { TranslocoModule } from '@ngneat/transloco';
import {MatCheckboxModule} from '@angular/material/checkbox';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-upload-rights-dialog',
  templateUrl: './upload-rights-dialog.component.html',
  styleUrls: ['./upload-rights-dialog.component.scss'],
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, MatCheckboxModule , TranslocoModule,FormsModule, CommonModule],
})
export class UploadRightsDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<UploadRightsDialogComponent>
  ) { }
  
  editorContent: string = '';
  terms: boolean = false;


  isValid(): boolean {
    return this.editorContent.length != 0 && this.terms;
  }

  closeDialog() {
    this.dialogRef.close();
  }

  onOkClick() {
    // Pass data back to the component that opened the dialog
    this.dialogRef.close({ result: 'OK' });
  }

  cancel() {
    this.dialogRef.close();
  }
}
