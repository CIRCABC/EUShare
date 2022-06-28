/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { ModalsService } from '../modals.service';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Recipient } from '../../../openapi';
import { recipientValidator } from '../../validators/recipient-validator';
import { UploadedFilesService } from '../../../services/uploaded-files.service';

@Component({
  selector: 'app-add-recipients-modal',
  templateUrl: './add-recipients-modal.component.html',
})
export class AddRecipientsModalComponent implements OnInit {
  public modalActive = false;
  public modalFileName = '';
  private modalFileId = '';
  public uploadInProgress = false;

  public sharedWithFormGroup!: UntypedFormGroup;

  public get email(): string {
    return this.sharedWithFormGroup.controls['email'].value;
  }

  public get message(): string {
    return this.sharedWithFormGroup.controls['message'].value;
  }

  constructor(
    private modalService: ModalsService,
    private fb: UntypedFormBuilder,
    private uploadedFileService: UploadedFilesService
  ) {}

  ngOnInit() {
    this.sharedWithFormGroup = this.fb.group(
      {
        message: [''],
        email: [''],
      },
      { validators: recipientValidator(), updateOn: 'change' }
    );
    this.modalService.activateAddRecipientsModal$.subscribe(
      (nextModalActiveValue) => {
        this.modalActive = nextModalActiveValue.modalActive;
        this.modalFileName = nextModalActiveValue.modalFileName;
        this.modalFileId = nextModalActiveValue.modalFileId;
      }
    );
  }

  public closeModal() {
    this.modalService.deactivateAddRecipientsModal();
  }

  public resetRecipient() {
    this.sharedWithFormGroup.controls['message'].reset();
    this.sharedWithFormGroup.controls['email'].reset();
  }

  async onSubmit() {
    this.uploadInProgress = true;
    const recipient: Recipient = {
      email: this.email,
      message: this.message,
    };

    await this.uploadedFileService.addOneRecipient(
      this.modalFileName,
      this.modalFileId,
      recipient
    );
    this.uploadInProgress = false;
  }
}
