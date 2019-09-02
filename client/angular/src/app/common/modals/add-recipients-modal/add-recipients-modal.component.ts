/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { ModalsService } from '../modals.service';
import { NotificationService } from '../../notification/notification.service';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';
import { FileService, Recipient } from '../../../openapi';
import { recipientValidator } from '../../validators/recipient-validator';

@Component({
  selector: 'app-add-recipients-modal',
  templateUrl: './add-recipients-modal.component.html',
  styleUrls: ['./add-recipients-modal.component.css']
})
export class AddRecipientsModalComponent implements OnInit {
  // tslint:disable-next-line:no-output-rename
  @Output('pullChanges')
  public valueChange = new EventEmitter();

  public modalActive = false;
  public modalFileName = '';
  private modalFileId = '';
  public uploadInProgress = false;

  public addRecipientsByEmailOrByLink = true;

  public sharedWithFormGroup!: FormGroup;

  public get sendEmail(): string {
    return this.sharedWithFormGroup.controls['sendEmail'].value;
  }

  public get sendEmailIsTrue(): boolean {
    return this.sendEmail === 'True';
  }

  public get email(): string {
    return this.sharedWithFormGroup.controls['email'].value;
  }

  public get name(): string {
    return this.sharedWithFormGroup.controls['name'].value;
  }

  public get message(): string {
    return this.sharedWithFormGroup.controls['message'].value;
  }

  public changeByEmailOrLink(emailOrLink: boolean): void {
    this.addRecipientsByEmailOrByLink = emailOrLink;
    this.resetRecipient();
  }

  constructor(
    private modalService: ModalsService,
    private notificationService: NotificationService,
    private fb: FormBuilder,
    private fileService: FileService
  ) {}

  ngOnInit() {
    this.sharedWithFormGroup = this.fb.group(
      {
        sendEmail: ['True', Validators.required],
        message: [''],
        email: [''],
        name: ['']
      },
      { validators: recipientValidator(), updateOn: 'change' }
    );
    this.modalService.activateAddRecipientsModal$.subscribe(
      nextModalActiveValue => {
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
    this.sharedWithFormGroup.controls['name'].reset();
  }

  public async onSubmit() {
    try {
      this.uploadInProgress = true;
      let recipient: Recipient = {
        emailOrName: 'emailOrName',
        sendEmail: false,
        message: 'message'
      };

      if (this.sendEmailIsTrue) {
        recipient = {
          emailOrName: this.email,
          sendEmail: this.sendEmailIsTrue,
          message: this.message
        };
      } else {
        recipient = {
          emailOrName: this.name,
          sendEmail: this.sendEmailIsTrue
        };
      }
      await this.fileService
        .postFileSharedWith(this.modalFileId, recipient)
        .toPromise();
      this.valueChange.emit(true);
    } catch (e) {
      this.notificationService.errorMessageToDisplay(
        e,
        'adding your recipient'
      );
      this.uploadInProgress = false;
      return;
    }
    this.notificationService.addSuccessMessage(
      'Succesfully added your recipient to ' + this.modalFileName
    );
    this.uploadInProgress = false;
  }
}
