/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { FileBasics } from '../../../openapi';
import { UploadedFilesService } from '../../../services/uploaded-files.service';
import { ModalsService } from '../modals.service';

@Component({
  selector: 'app-change-expiration-date-modal',
  templateUrl: './change-expiration-date-modal.component.html',
  styleUrls: ['./change-expiration-date-modal.component.scss'],
})
export class ChangeExpirationDateModalComponent implements OnInit {
  public modalActive!: boolean;
  public modalFileId!: string;
  public modalFileName!: string;
  public expirationDate!: string;
  public isLoading = false;

  constructor(private modalService: ModalsService,private uploadedFileService: UploadedFilesService) {}

  ngOnInit() {
    this.modalActive = false;
    this.modalService.activateChangeExpirationDateModal$.subscribe(
      (nextModalActiveValue) => {
        this.modalActive = nextModalActiveValue.modalActive;
        this.modalFileId = nextModalActiveValue.modalFileId;
        this.modalFileName = nextModalActiveValue.modalFileName;
        this.expirationDate = (nextModalActiveValue.expirationDate).toString().replace(new RegExp(',', 'g'), '-') ;
      }
    );
  }

  public async save(){
     this.isLoading = true;
     const file: FileBasics = {
      hasPassword:false,
      size:0,
      name:'',
      expirationDate: this.expirationDate,
    };
     await this.uploadedFileService.updateOneFile(this.modalFileId,file)
    await this.uploadedFileService.update()
     this.isLoading = false;

  }

  public closeModal() {
    this.modalService.deactivateChangeExpirationDateModal();
  }
}
