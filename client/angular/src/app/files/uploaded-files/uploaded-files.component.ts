import { Component, OnInit } from '@angular/core';
import { UsersService, FileInfoUploader } from '../../openapi';
import { ActivatedRoute } from '@angular/router';
import { NotificationService } from '../../common/notification/notification.service';

@Component({
  selector: 'app-uploaded-files',
  templateUrl: './uploaded-files.component.html',
})
export class UploadedFilesComponent implements OnInit {

  private userId!: string;
  private pageSize = 10;
  private pageNumber = 0;
  public userName!: string

  public fileInfoUploaderArray!: FileInfoUploader[];
  private fileInfoUploaderArrayPrevious !: FileInfoUploader[];
  private fileInfoUploaderArrayNext !: FileInfoUploader[];

  constructor(private route: ActivatedRoute,
    private userService: UsersService,
    private notificationService: NotificationService) {
  }

  async ngOnInit() {
    const userIdOrNull = this.route.snapshot.paramMap.get('userId');
    const userNameOrNull = this.route.snapshot.paramMap.get('userName');
    if (userIdOrNull) {
      this.userId = userIdOrNull;
      this.fileInfoUploaderArray = await this.userService.getFilesFileInfoUploader(this.userId, this.pageSize, this.pageNumber).toPromise();
    } else {
      this.notificationService.addErrorMessage('A problem occured while downloading files information. Please contact the support.');
    }
    if (userNameOrNull) {
      this.userName = userNameOrNull;
    }
  }

  destroyOneFile(fileId: string) {
    this.fileInfoUploaderArray = this.fileInfoUploaderArray.filter(file => file.fileId !== fileId);
  }

}
