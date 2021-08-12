/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit, Input } from '@angular/core';
import { UploadedFilesService } from '../../../services/uploaded-files.service';
import { ModalsService } from '../../modals/modals.service';

@Component({
  selector: 'app-delete-button',
  templateUrl: './delete-button.component.html',
  styleUrls: ['./delete-button.component.scss'],
})
export class DeleteButtonComponent implements OnInit {
  // tslint:disable-next-line:no-input-rename
  @Input('fileId')
  private fileId!: string;

  // tslint:disable-next-line:no-input-rename
  @Input('fileName')
  public fileName!: string;

  public isLoading = false;

  constructor(
    private uploadedFileService: UploadedFilesService,
    private modalService: ModalsService
  ) {}

  public async delete() {
    this.isLoading = true;
    await this.uploadedFileService.removeOneFile(this.fileId, this.fileName);
    this.isLoading = false;
    this.modalService.deactivateDeleteConfirmModal();
  }

  ngOnInit(): void {}
}
