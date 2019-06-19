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
import { faTrashAlt } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-my-shared-files',
  templateUrl: './my-shared-files.component.html',
  styleUrls: ['./my-shared-files.component.css']
})
export class MySharedFilesComponent implements OnInit {

  faLock = faLock;
  faTrashAlt = faTrashAlt;

  fileIds = Array<string>();
  fileInfoUploaderArray = Array<FileInfoUploader>();
  loading = false;
  modalActive = false;
  modalError = false;
  modalFileId = '';
  modalFileName = '';
  myName!: string;

  constructor(private session: SessionService, private userApi: UsersService, private fileApi: FileService, private router: Router, private notificationService: NotificationService) { }

  ngOnInit() {
    const id = this.session.getStoredId();
    if (id) {
      this.userApi.getUserUserInfo(id).toPromise().then(userInfo => {
        this.myName = userInfo.name;
      }).catch(error => {
        this.notificationService.errorMessageToDisplay(error, 'downloading your user\'s information');
      })
      this.userApi.getFilesFileInfoUploader(id, 10, 0).toPromise().then(fileInfoUploaderArray => {
        this.fileInfoUploaderArray = fileInfoUploaderArray;
      }).catch(error => {
        this.notificationService.errorMessageToDisplay(error, 'fetching your uploaded files');
      });
    } else {
      this.router.navigateByUrl('login');
    }

  }

  public download(fileId: string, fileName: string, filePassword?: string) {
    this.fileApi.getFile(fileId, filePassword).toPromise().then(file => {
      saveAs(file, fileName);
    }).catch(error => {
      this.notificationService.errorMessageToDisplay(error, 'downloading your file');
    });
  }


  public openModal(fileId: string, fileName: string) {
    this.modalActive = true;
    this.modalFileId = fileId;
    this.modalFileName = fileName;
  }

  public closeModal() {
    this.modalActive = false;
  }

  public delete(i: number) {
    this.fileApi.deleteFile(this.fileInfoUploaderArray[i].fileId).toPromise().then(success => {
      this.fileInfoUploaderArray.splice(i, 1);
      this.notificationService.addSuccessMessage('Successfully deleted file named ' + this.fileInfoUploaderArray[i].name);
    }).catch(error => {
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
