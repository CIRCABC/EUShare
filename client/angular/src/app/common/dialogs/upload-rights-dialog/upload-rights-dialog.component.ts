import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-upload-rights-dialog',
  templateUrl: './upload-rights-dialog.component.html',
  styleUrls: ['./upload-rights-dialog.component.scss'],
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
})
export class UploadRightsDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<UploadRightsDialogComponent>
  ) { }
  result="hello2";
  closeDialog() {
    this.dialogRef.close();
  }

  onOkClick() {
    // Pass data back to the component that opened the dialog
    this.dialogRef.close({ result: 'OK' });
  }
}
