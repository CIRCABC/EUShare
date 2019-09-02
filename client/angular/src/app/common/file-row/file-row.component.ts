import { Component, Input, Output, EventEmitter } from '@angular/core';
import {
  faFile, faEllipsisH, faLock
} from '@fortawesome/free-solid-svg-icons';
import { FileInfoUploader, FileService } from '../../openapi';
import { ModalsService } from '../modals/modals.service';
import { NotificationService } from '../notification/notification.service';

@Component({
  selector: 'app-file-row',
  templateUrl: './file-row.component.html',
  styleUrls: ['./file-row.component.scss']
})
export class FileRowComponent {
  @Input('fileToDisplay')
  public file!: FileInfoUploader;

  @Input('displayAddRecipientsButton')
  public displayAddRecipients: boolean = false;

  @Output('onDestroy')
  public onDestroy: EventEmitter<string> = new EventEmitter<string>()

  public isMoreDisplayed: boolean = false;

  public faFile = faFile;
  public faEllipsisH = faEllipsisH;
  public faLock = faLock;

  constructor(private modalService: ModalsService, private fileApi: FileService, private notificationService: NotificationService) { }

  public openDownloadModal(fileId: string, fileName: string, fileHasPassword: boolean) {
    this.modalService.activateDownloadModal(fileId, fileName, fileHasPassword);
  }

  public openAddRecipientsModal(fileName: string, fileId: string) {
    this.modalService.activateAddRecipientsModal(fileName, fileId);
  }

  public delete() {
    this.fileApi
      .deleteFile(this.file.fileId)
      .toPromise()
      .then(success => {
        this.notificationService.addSuccessMessage(
          'Successfully deleted file named ' + this.file.name
        );
        this.onDestroy.next(this.file.fileId);
      })
      .catch(error => {
        this.notificationService.errorMessageToDisplay(
          error,
          'deleting your file'
        );
      });
  }

  public displayRecipients() {
    this.file.sharedWith.length >= 1 && this.modalService.activateShareWithUsersModal(this.file.name, this.file.fileId, this.file.sharedWith, this.file.hasPassword);
  }

  public displayMore() {
    this.isMoreDisplayed = true;
  }

  public displayLess() {
    this.isMoreDisplayed = false;
  }

}
