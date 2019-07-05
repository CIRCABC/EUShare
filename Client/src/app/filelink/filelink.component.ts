/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { FileService } from '../openapi';
import { NotificationService } from '../common/notification/notification.service';
import { ModalsService } from '../common/modals/modals.service';

@Component({
  selector: 'app-filelink',
  templateUrl: './filelink.component.html',
  styleUrls: ['./filelink.component.css']
})
export class FilelinkComponent implements OnInit {
  public fileName!: string;
  private isFilePasswordProtected!: boolean;
  private fileId!: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router, private fileService: FileService, private notificationService: NotificationService, private modalService: ModalsService) {
  }

  download() {
    if (this.isFilePasswordProtected) {
      this.modalService.activatePasswordModal(this.fileId, this.fileName);
    } else {
      this.fileService.getFile(this.fileId)
        .toPromise().then(blob => {
          saveAs(blob, this.fileName);
        }).catch(error => {
          this.notificationService.errorMessageToDisplay(error, 'downloading your file');
        });
    }
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    const fileNameB64URIEncoded = this.route.snapshot.paramMap.get('filenameb64');
    const isPasswordProtected = this.route.snapshot.paramMap.get('isPasswordProtected');
    if (id && fileNameB64URIEncoded && isPasswordProtected) {
      this.fileId = id;
      this.fileName = atob(decodeURIComponent(fileNameB64URIEncoded));
      this.isFilePasswordProtected = (isPasswordProtected === '1');
    } else {
      this.router.navigateByUrl('/home');
    }
  }
}
