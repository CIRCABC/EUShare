/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { FileService } from '../../../openapi';
import { NotificationService } from '../../notification/notification.service';
import { ModalsService } from '../modals.service';

@Component({
  selector: 'app-password-modal',
  templateUrl: './password-modal.component.html'
})
export class PasswordModalComponent implements OnInit {
  public modalActive!: boolean;
  public isWhat = 'is-info';
  public isLoading = false;
  private modalFileId!: string;
  private modalFileName!: string;

  constructor(
    private fileApi: FileService,
    private notificationService: NotificationService,
    private modalService: ModalsService
  ) {}

  ngOnInit() {
    this.modalActive = false;
    this.modalService.activatePasswordModal$.subscribe(nextModalActiveValue => {
      this.modalActive = nextModalActiveValue.modalActive;
      this.modalFileId = nextModalActiveValue.modalFileId;
      this.modalFileName = nextModalActiveValue.modalFileName;
    });
  }

  public closeModal() {
    this.modalService.deactivatePasswordModal();
  }

  public download(filePassword?: string) {
    this.isLoading = true;
    this.fileApi
      .getFile(this.modalFileId, filePassword)
      .toPromise()
      .then(file => {
        this.isWhat = 'is-success';
        saveAs(file, this.modalFileName);
        this.isLoading = false;
      })
      .catch(error => {
        if (error.status === 401) {
          this.isWhat = 'is-danger';
        }
        this.notificationService.errorMessageToDisplay(
          error,
          'downloading your file'
        );
        this.isLoading = false;
      });
  }
}
