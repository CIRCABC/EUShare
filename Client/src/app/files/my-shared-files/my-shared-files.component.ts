/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { faLock } from '@fortawesome/free-solid-svg-icons';
import { FileInfoUploader, SessionService, FileService, MeService, UserInfo } from '../../openapi';
import { Router } from '@angular/router';

@Component({
  selector: 'app-my-shared-files',
  templateUrl: './my-shared-files.component.html',
  styleUrls: ['./my-shared-files.component.css']
})
export class MySharedFilesComponent implements OnInit {

  faLock = faLock;

  fileIds = Array<string>();
  files = Array<FileInfoUploader>();
  loading = false;
  modalActive = false;
  modalError = false;
  modalFileId = '';
  modalFileName = '';
  myId!: string;

  constructor(private session: SessionService, private meApi: MeService, private fileApi: FileService, private router: Router) { }

  ngOnInit() {
    const id = this.session.getStoredId();
    if (id) {
      this.myId = id;
      this.meApi.getUserInfo().subscribe(
        userInfo => {
          this.files = new Array();
          this.fileIds = new Array();
          userInfo.uploadedFiles.forEach(str => {
            this.fileApi.getFileFileInfoUploader(str).subscribe(fileInfoUploader => {
              this.files.push(fileInfoUploader);
              this.fileIds.push(str);
            });
          });
        }
      );
    } else {
      this.router.navigateByUrl('login');
    }

  }

  public download(fileId: string, fileName: string, filePassword?: string) {
    this.fileApi.getFile(fileId, filePassword).subscribe(file => {
      saveAs(file, fileName);
    })
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
    this.fileApi.deleteFile(this.fileIds[i]).subscribe(value => {
      this.fileIds.splice(i, 1);
      this.files.splice(i, 1);
    });
  }

}
