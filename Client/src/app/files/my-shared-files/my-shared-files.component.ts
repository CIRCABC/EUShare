/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { faLock } from '@fortawesome/free-solid-svg-icons';
import { FileInfoUploader, SessionService, FileService, UsersService, UserInfo } from '../../openapi';
import { Router } from '@angular/router';
import { NotificationService } from '../../common/notification/notification.service';
import { faUserTimes, faExternalLinkAlt } from '@fortawesome/free-solid-svg-icons';
import { ModalsService } from '../../common/modals/modals.service';
import { environment } from '../../../environments/environment.prod';

@Component({
  selector: 'app-my-shared-files',
  templateUrl: './my-shared-files.component.html',
  styleUrls: ['./my-shared-files.component.css']
})
export class MySharedFilesComponent implements OnInit {

  public faLock = faLock;
  public faUserTimes = faUserTimes;
  public faExternalLinkAlt = faExternalLinkAlt;

  public fileInfoUploaderArray!: FileInfoUploader[];
  private nextFileInfoUploaderArray!: FileInfoUploader[];
  private previousFileInfoUploaderArray!: FileInfoUploader[];
  private myId!: string;
  private displayedPageNumber!: number;

  constructor(private session: SessionService, private userApi: UsersService, private fileApi: FileService, private router: Router, private notificationService: NotificationService,
    private modalService: ModalsService) { }


  public ngOnInit() {
    const id = this.session.getStoredId();
    if (id) {
      this.myId = id;
      this.userApi.getFilesFileInfoUploader(id, 10, 0).toPromise().then(result => {
        this.fileInfoUploaderArray = result;
        if (this.fileInfoUploaderArray.length === 10) {
          this.userApi.getFilesFileInfoUploader(id, 10, 1).toPromise().then(result => {
            this.nextFileInfoUploaderArray = result;
          }).catch(error => {
            this.notificationService.errorMessageToDisplay(error, 'fetching your uploaded files');
          });
        }
      }).catch(error => {
        this.notificationService.errorMessageToDisplay(error, 'fetching your uploaded files');
      });
    } else {
      this.router.navigateByUrl('');
    }
  }

  public displayNextFileInfoUploaderArray() {
    this.previousFileInfoUploaderArray = Array.from(this.fileInfoUploaderArray);
    this.fileInfoUploaderArray = Array.from(this.nextFileInfoUploaderArray);
  }


  public download(fileId: string, fileName: string, filePassword?: string) {
    this.fileApi.getFile(fileId, filePassword).toPromise().then(file => {
      saveAs(file, fileName);
    }).catch(error => {
      this.notificationService.errorMessageToDisplay(error, 'downloading your file');
    });
  }

  public openPasswordModal(fileId: string, fileName: string) {
    this.modalService.activatePasswordModal(fileId, fileName);
  }

  public openFileLinkModal(downloadLink: string, fileName: string, isPasswordProtected: boolean) {
    let fileLinkBuild = environment.frontend_url + '/filelink/' + downloadLink + '/' + encodeURIComponent(btoa(fileName)) + '/';
    if (isPasswordProtected) {
      fileLinkBuild = fileLinkBuild + '1';
    } else {
      fileLinkBuild = fileLinkBuild + '0';
    }
    this.modalService.activateFileLinkModal(fileLinkBuild);
  }

  public delete(i: number) {
    this.fileApi.deleteFile(this.fileInfoUploaderArray[i].fileId).toPromise().then(success => {
      const deletedFileName:string = this.fileInfoUploaderArray[i].name;
      this.fileInfoUploaderArray.splice(i, 1);
      this.notificationService.addSuccessMessage('Successfully deleted file named ' + deletedFileName);
    }).catch(error => {
      console.log(error);
      this.notificationService.errorMessageToDisplay(error, 'deleting your file');
    });
  }

  public deleteShare(fileId: string, fileName: string, shareId: string, shareName: string, fileIndex: number, shareIndex: number) {
    this.fileApi.deleteFileSharedWithUser(fileId, shareId).toPromise().then(success => {
      this.fileInfoUploaderArray[fileIndex].sharedWith.splice(shareIndex, 1);
      this.notificationService.addSuccessMessage('Successfully removed file ' + fileName + ' \'s share with ' + shareName);
    }).catch(error => {
      this.notificationService.errorMessageToDisplay(error, 'removing the file\'s share');
    });
  }


}
