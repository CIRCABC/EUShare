import { Component, OnInit } from '@angular/core';
import { ModalsService } from '../modals.service';
import { RecipientWithLink, FileService } from '../../../openapi';
import { NotificationService } from '../../notification/notification.service';

@Component({
  selector: 'app-share-with-users-modal',
  templateUrl: './share-with-users-modal.component.html',
  styleUrls: ['./share-with-users-modal.component.scss'],
  preserveWhitespaces: true
})
export class ShareWithUsersModalComponent implements OnInit {

  public modalActive: boolean = false
  public modalFileName: string = '';
  private modalFileId: string = '';
  public recipientsWithLink: RecipientWithLink[] = [];
  private modalFileIsPasswordProtected: boolean = false;

  constructor(private modalService: ModalsService, private fileApi: FileService, private notificationService: NotificationService) { }

  public closeModal() {
    this.modalService.deactivateShareWithUsersModal();
  }


  ngOnInit() {
    this.modalActive = false;
    this.modalService.activateShareWithUsersModal$.subscribe(nextModalActiveValue => {
      this.modalActive = nextModalActiveValue.modalActive;
      this.modalFileId = nextModalActiveValue.modalFileId;
      this.modalFileName = nextModalActiveValue.modalFileName;
      this.modalFileIsPasswordProtected = nextModalActiveValue.modalFileHasPassword;
      this.recipientsWithLink = nextModalActiveValue.recipientsWithLink;
    });
  }

  public deleteShare(
    shareId: string,
    shareName: string,
    shareIndex: number
  ) {
    this.fileApi
      .deleteFileSharedWithUser(this.modalFileId, shareId)
      .toPromise()
      .then(success => {
        this.recipientsWithLink.splice(shareIndex, 1);
        this.notificationService.addSuccessMessage(
          'Successfully removed file ' +
          this.modalFileName +
          " 's share with " +
          shareName
        );
      })
      .catch(error => {
        this.notificationService.errorMessageToDisplay(
          error,
          "removing the file's share"
        );
      });
  }

  public copyLink(i: number) {
    let selBox = document.createElement('textarea');
    selBox.style.position = 'fixed';
    selBox.style.left = '0';
    selBox.style.top = '0';
    selBox.style.opacity = '0';
    selBox.value = this.formatLink(i);
    document.body.appendChild(selBox);
    selBox.focus();
    selBox.select();
    document.execCommand('copy');
    document.body.removeChild(selBox);
    this.notificationService.addSuccessMessage('Copied file link !', true);
  }

  private formatLink(i: number) {
    let isPasswordProtected = this.modalFileIsPasswordProtected;
    const fileLinkWithoutFiles = window.location.href.slice(0, window.location.href.lastIndexOf('/'));
    const fileLinkWithoutFileId = fileLinkWithoutFiles.slice(0, fileLinkWithoutFiles.lastIndexOf('/'));
    const fileLinkWithoutAdministration = fileLinkWithoutFileId.slice(0, fileLinkWithoutFileId.lastIndexOf('/'));
    let fileLinkBuild =
    fileLinkWithoutAdministration +
      '/filelink/' +
      this.recipientsWithLink[i].downloadLink +
      '/' +
      encodeURIComponent(btoa(this.modalFileName)) +
      '/';
    fileLinkBuild = isPasswordProtected
      ? fileLinkBuild + '1'
      : fileLinkBuild + '0';
    return fileLinkBuild;
  }


}
