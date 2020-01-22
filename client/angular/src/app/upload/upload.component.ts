/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { globalValidator } from './upload.validators';
import { Component, OnInit } from '@angular/core';
import {
  FormArray,
  FormGroup,
  Validators,
  AbstractControl,
  FormBuilder,
  FormControl
} from '@angular/forms';
import {
  FileService,
  FileRequest,
  Recipient,
  UsersService,
  SessionService
} from '../openapi';
import { NotificationService } from '../common/notification/notification.service';
import { fileSizeValidator } from '../common/validators/file-validator';
import { map } from 'rxjs/operators';
import {
  HttpEvent,
  HttpEventType,
  HttpErrorResponse
} from '@angular/common/http';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.scss']
})
export class UploadComponent implements OnInit {
  public moreOptions = false;
  public uploadInProgress = false;
  public uploadform!: FormGroup;
  public shareWithUser = '';
  public leftSpaceInBytes = 0;
  public totalSpaceInBytes = 0;
  public percentageUploaded = 0;

  constructor(
    private fb: FormBuilder,
    private sessionApi: SessionService,
    private userApi: UsersService,
    private fileApi: FileService,
    private notificationService: NotificationService
  ) {
    this.initializeFormGroup();
  }
  async initializeAvailableSpace() {
    const id = this.sessionApi.getStoredId();
    const userInfoStored = this.sessionApi.getStoredUserInfo();
    if (userInfoStored) {
      this.totalSpaceInBytes = userInfoStored.totalSpace;
      this.leftSpaceInBytes = userInfoStored.totalSpace - userInfoStored.usedSpace;
    }

    if (id) {
      try {
        const me = await this.userApi.getUserUserInfo(id).toPromise();
        this.leftSpaceInBytes =
          me && me.totalSpace - me.usedSpace > 0
            ? me.totalSpace - me.usedSpace
            : 0;
        this.totalSpaceInBytes = me.totalSpace;
        this.sessionApi.setStoredUserInfo(me);
      } catch (error) {
        this.notificationService.errorMessageToDisplay(
          error,
          'retrieving your user informations'
        );
      }
    }
  }

  initializeEventListeners() {
    window.addEventListener(
      'dragover',
      e => {
        e.preventDefault();
      },
      false
    );
    window.addEventListener(
      'drop',
      e => {
        e.preventDefault();
      },
      false
    );
  }

  initializeFormGroup() {
    this.uploadform = this.fb.group(
      {
        fileFromDisk: [
          undefined,
          Validators.compose([fileSizeValidator(this.leftSpaceInBytes)])
        ],
        emailOrLink: ['', Validators.required],
        emailsWithMessages: this.fb.array([
          // this.initializedEmailsWithMessages()
        ]),
        namesOnly: this.fb.array([
          // this.initializeNamesOnly()
        ]),
        expirationDate: [this.get7DaysAfterToday(), Validators.required],
        password: [undefined]
      },
      { validators: globalValidator() }
    );
  }

  initializedEmailsWithMessages(): FormGroup {
    return this.fb.group(
      {
        email: new FormControl('', Validators.required),
        message: ['']
      },
      { updateOn: 'blur' }
    );
  }

  initializeNamesOnly(): FormGroup {
    return this.fb.group(
      {
        name: new FormControl('', Validators.required)
      },
      { updateOn: 'change' }
    );
  }

  async ngOnInit() {
    this.initializeEventListeners();
    await this.initializeAvailableSpace();
    this.initializeFormGroup();
  }

  // SELECT IMPORT
  getSelectImport(): string {
    return this.uploadform.controls['selectImport'].value;
  }

  // DRAG AND DROP
  drop(event: any): void {
    const file: File = event.dataTransfer.items[0].getAsFile();
    this.setFileFromDisk(file);
  }
  getFileFromDisk(): File {
    return this.uploadform.controls['fileFromDisk'].value;
  }
  setFileFromDisk(file: File): void {
    this.uploadform.controls['fileFromDisk'].setValue(file);
  }
  resetFileFromDisk(): void {
    this.uploadform.controls['fileFromDisk'].reset();
  }

  // EMAIL OR LINK
  getEmailOrLink(): string {
    return this.uploadform.controls['emailOrLink'].value;
  }
  emailOrLinkIsEmail(): boolean {
    return this.getEmailOrLink() === 'Email';
  }
  emailOrLinkIsLink(): boolean {
    return this.getEmailOrLink() === 'Link';
  }

  // EMAIL WITH MESSAGES
  get emailsWithMessages() {
    return this.uploadform.get('emailsWithMessages') as FormArray;
  }
  addEmailsWithMessages() {
    this.emailsWithMessages.push(this.initializedEmailsWithMessages());
  }
  deleteEmailsWithMessages(i: number) {
    this.emailsWithMessages.removeAt(i);
  }
  resetEmailsWithMessages() {
    const formArray: FormArray = this.emailsWithMessages;
    while (formArray.length !== 0) {
      formArray.removeAt(0);
    }
  }

  getEmailsWithMessagesFormgroupNumber(): number {
    const formArray: FormArray = <FormArray>(
      this.uploadform.controls['emailsWithMessages']
    );
    if (formArray) {
      return formArray.controls.length;
    }
    return 0;
  }
  getEmailsWithMessagesFormgroup(i: number): FormGroup | null {
    const formArrayOrNull: FormArray | null = <FormArray | null>(
      this.uploadform.controls['emailsWithMessages']
    );
    if (formArrayOrNull) {
      return <FormGroup | null>formArrayOrNull.controls[i];
    }
    return null;
  }

  getEmailsWithMessagesOnlyMessage(i: number): AbstractControl | null {
    return this.getEmailsWithMessagesOnlyOne(i, 'message');
  }

  getEmailsWithMessagesOnlyEmail(i: number): AbstractControl | null {
    return this.getEmailsWithMessagesOnlyOne(i, 'email');
  }

  getEmailsWithMessagesOnlyOne(i: number, emailOrMessage: string) {
    const formGroup = this.getEmailsWithMessagesFormgroup(i);
    if (formGroup) {
      return formGroup.controls[emailOrMessage];
    }
    return null;
  }

  // NAMES ONLY
  get namesOnly() {
    return this.uploadform.get('namesOnly') as FormArray;
  }
  addNamesOnly() {
    this.namesOnly.push(this.initializeNamesOnly());
  }
  deleteNamesOnly(i: number) {
    this.namesOnly.removeAt(i);
  }
  resetNamesOnly() {
    const formArray: FormArray = this.namesOnly;
    while (formArray.length !== 0) {
      formArray.removeAt(0);
    }
  }
  getNamesOnlyFormgroupNumber(): number {
    const formArray: FormArray = this.uploadform.controls[
      'namesOnly'
    ] as FormArray;
    if (formArray) {
      return formArray.controls.length;
    }
    return 0;
  }
  getNamesOnlyFormgroup(i: number): FormGroup | null {
    const formArray: FormArray = this.uploadform.controls[
      'namesOnly'
    ] as FormArray;
    if (formArray) {
      return formArray.controls[i] as FormGroup;
    }
    return null;
  }

  resetRecipients() {
    this.resetEmailsWithMessages();
    this.resetNamesOnly();
  }

  // MORE OPTIONS
  moreOptionsChange() {
    this.moreOptions = !this.moreOptions;
    this.resetExpirationDate();
    this.resetPassword();
  }

  // EXPIRATION DATE
  getExpirationDate(): Date {
    return this.uploadform.controls['expirationDate'].value;
  }
  resetExpirationDate(): void {
    this.uploadform.controls['expirationDate'].setValue(
      this.get7DaysAfterToday()
    );
  }
  getTomorrow(): Date {
    const milliseconds = Date.now() + 1 * 24 * 60 * 60 * 1000;
    return new Date(milliseconds);
  }
  get7DaysAfterToday(): Date {
    const milliseconds = Date.now() + 7 * 24 * 60 * 60 * 1000;
    return new Date(milliseconds);
  }

  // PASSWORD
  getPassword(): string {
    return this.uploadform.controls['password'].value;
  }
  resetPassword(): void {
    this.uploadform.controls['password'].reset();
  }

  async submit() {
    this.uploadInProgress = true;
    if (this.getFileFromDisk()) {
      try {
        const recipientArray = new Array<Recipient>();
        if (this.emailOrLinkIsEmail()) {
          for (
            let i = 0;
            i < this.getEmailsWithMessagesFormgroupNumber();
            i++
          ) {
            const formGroupOrNull: FormGroup | null = this.getEmailsWithMessagesFormgroup(
              i
            );
            if (formGroupOrNull) {
              const message: string = formGroupOrNull.controls['message'].value;
              const email: string = formGroupOrNull.controls['email'].value;
              const recipient: Recipient = {
                emailOrName: email,
                sendEmail: this.emailOrLinkIsEmail()
              };
              if (message && message !== '' && this.emailOrLinkIsEmail) {
                recipient.message = message;
              }
              recipientArray.push(recipient);
            } else {
              this.notificationService.addErrorMessage(
                'A problem occured while uploading your file, please try again later or contact the support',
                false
              );
              return;
            }
          }
        } else {
          for (let i = 0; i < this.getNamesOnlyFormgroupNumber(); i++) {
            const formGroupOrNull = this.getNamesOnlyFormgroup(i);
            if (formGroupOrNull) {
              const name: string = formGroupOrNull.controls['name'].value;
              const recipient: Recipient = {
                emailOrName: name,
                sendEmail: this.emailOrLinkIsEmail()
              };
              recipientArray.push(recipient);
            } else {
              this.notificationService.addErrorMessage(
                'A problem occured while uploading your file, please try again later or contact the support',
                false
              );
              return;
            }
          }
        }
        const myFileRequest: FileRequest = {
          expirationDate: this.getExpirationDate()
            .toISOString()
            .substring(0, 10),
          hasPassword: this.getPassword() != null && this.getPassword() !== '',
          name: this.getFileFromDisk().name,
          size: this.getFileFromDisk().size,
          sharedWith: recipientArray
        };
        if (this.getPassword() !== '') {
          myFileRequest.password = this.getPassword();
        }
        const fileId = await this.fileApi
          .postFileFileRequest(myFileRequest)
          .toPromise();
        await this.fileApi
          .postFileContent(fileId, this.getFileFromDisk(), 'events', true)
          .pipe(map(event => this.getEventMessage(event)))
          .toPromise();
        if (this.emailOrLinkIsEmail()) {
          this.notificationService.addSuccessMessage(
            'Your recipients have been notified by mail that they may download the shared file!',
            false
          );
        } else {
          this.notificationService.addSuccessMessage(
            'Please find for each of your recipients, a personnal download link on the My Shared Files page',
            false
          );
        }
        await this.initializeAvailableSpace();
      } catch (e) {
        this.notificationService.errorMessageToDisplay(
          e,
          'uploading your file'
        );
        this.uploadInProgress = false;
        return;
      }
    }
    this.uploadInProgress = false;
    this.initializeFormGroup();
    this.notificationService.addSuccessMessage(
      'Your upload was successful!',
      true
    );
  }

  get uf() {
    return this.uploadform.controls;
  }

  private getEventMessage(event: HttpEvent<any>) {
    switch (event.type) {
      case HttpEventType.Sent:
        return;

      case HttpEventType.UploadProgress:
        let eventTotalOrUndefined = event.total;
        if (eventTotalOrUndefined === undefined) {
          eventTotalOrUndefined = 1;
        }
        const percentDone = Math.round(
          (event.loaded * 100) / eventTotalOrUndefined
        );
        this.percentageUploaded = percentDone;
        return;

      case HttpEventType.Response:
        if (event.status === 200) {
          this.notificationService.addSuccessMessage(
            `File successfully shared!`, true, 5
          );
        } else {
          this.notificationService.errorMessageToDisplay(
            event.body as HttpErrorResponse,
            'uploading the file'
          );
        }
        this.uploadInProgress = false;
        this.percentageUploaded = 0;
        return;

      case HttpEventType.ResponseHeader:
        if (event.status === 400) {
          this.notificationService.addErrorMessage(
            'The server could not understand your request.'
          );
          this.uploadInProgress = false;
          this.percentageUploaded = 0;
          return;
        }
        if (event.status === 401 || event.status === 403) {
          this.notificationService.addErrorMessage(
            'You are not authorized to perform this action.'
          );
          this.uploadInProgress = false;
          this.percentageUploaded = 0;
          return;
        }
        if (event.status === 500) {
          this.notificationService.addErrorMessage(
            'An error occured while downloading the file. Please contact the support.'
          );
          this.uploadInProgress = false;
          this.percentageUploaded = 0;
          return;
        }
        return;

      case HttpEventType.DownloadProgress:
        return;

      default:
        this.notificationService.addErrorMessage(
          'An error occured while downloading the file. Please contact the support.' +
          JSON.stringify(event)
        );
        this.uploadInProgress = false;
        this.percentageUploaded = 0;
        return;
    }
  }
}
