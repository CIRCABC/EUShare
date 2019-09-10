import { Component, Input, OnInit } from '@angular/core';
import { FileInfoUploader, UsersService } from '../../openapi';
import { NotificationService } from '../notification/notification.service';

@Component({
  selector: 'app-uploaded-file-row-container',
  templateUrl: './uploaded-file-row-container.component.html',
})
export class FileRowContainerComponent implements OnInit {

  @Input('userId')
  private userId!: string;

  @Input('displayAsAdministrator')
  public displayAsAdministrator = false;

  @Input('displayAsUploader')
  public displayAsUploader = true;

  private pageSize = 10;
  private pageNumber = 0;

  public fileInfoUploaderArray!: FileInfoUploader[];
  private fileInfoUploaderArrayPrevious!: FileInfoUploader[];
  private fileInfoUploaderArrayNext!: FileInfoUploader[];

  constructor(
    private userService: UsersService,
    private notificationService: NotificationService
  ) { }

  async ngOnInit() {
    if (this.userId) {
      this.fileInfoUploaderArray = await this.userService
        .getFilesFileInfoUploader(this.userId, this.pageSize, this.pageNumber)
        .toPromise();
    } else {
      this.notificationService.addErrorMessage(
        'A problem occured while downloading files information. Please contact the support.'
      );
    }
  }

  destroyOneFile(fileId: string) {
    this.fileInfoUploaderArray = this.fileInfoUploaderArray.filter(
      file => file.fileId !== fileId
    );
  }


}
