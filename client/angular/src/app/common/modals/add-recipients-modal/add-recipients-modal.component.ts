/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { ModalsService } from '../modals.service';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Recipient } from '../../../openapi';
import { recipientValidator } from '../../validators/recipient-validator';
import { UploadedFilesService } from '../../../services/uploaded-files.service';
import { SlicePipe } from '@angular/common';
import { TranslocoModule } from '@ngneat/transloco';
import { MessageTextAreaComponent } from '../../formComponents/message-text-area/message-text-area.component';
import { EmailInputComponent } from '../../formComponents/email-input/email-input.component';

@Component({
  selector: 'app-add-recipients-modal',
  templateUrl: './add-recipients-modal.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    EmailInputComponent,
    MessageTextAreaComponent,
    TranslocoModule,
    SlicePipe,
  ],
})
export class AddRecipientsModalComponent implements OnInit {
  public modalActive = false;
  public modalFileName = '';
  private modalFileId = '';
  public uploadInProgress = false;

  public sharedWithFormGroup!: FormGroup;

  public get email(): string {
    return this.sharedWithFormGroup.controls['email'].value;
  }

  public get message(): string {
    return this.sharedWithFormGroup.controls['message'].value;
  }

  public get downloadNotification(): boolean {
    return this.sharedWithFormGroup.controls['downloadNotification'].value;
  }

  constructor(
    private modalService: ModalsService,
    private fb: FormBuilder,
    private uploadedFileService: UploadedFilesService
  ) {}

  ngOnInit() {
    this.sharedWithFormGroup = this.fb.nonNullable.group(
      {
        message: [''],
        email: [''],
        downloadNotification: [false],
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
    this.sharedWithFormGroup.controls['downloadNotification'].reset();
  }

  async onSubmit() {
    this.uploadInProgress = true;
    const recipient: Recipient = {
      email: this.email,
      message: this.message,
      downloadNotification: this.downloadNotification,
    };

    await this.uploadedFileService.addOneRecipient(
      this.modalFileName,
      this.modalFileId,
      recipient
    );
    this.uploadInProgress = false;
  }
}
