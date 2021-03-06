/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import {
  FormArray,
  FormGroup,
  Validators,
  FormBuilder,
  FormControl
} from '@angular/forms';
import { faUpload, faUserSlash } from '@fortawesome/free-solid-svg-icons';
import {
  FileService,
  FileRequest,
  Recipient,
  UsersService,
  SessionService,
  FileInfoUploader
} from '../openapi';
import { NotificationService } from '../common/notification/notification.service';
import { fileSizeValidator } from '../common/validators/file-validator';
import { map } from 'rxjs/operators';
import {
  HttpEvent,
  HttpEventType,
  HttpErrorResponse
} from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.scss']
})
export class UploadComponent implements OnInit {
  public faUserSlash = faUserSlash;
  public faUpload = faUpload;
  public moreOptions = false;
  public uploadInProgress = false;
  public uploadform!: FormGroup;
  public shareWithUser = '';
  public leftSpaceInBytes = 0;
  public totalSpaceInBytes = 0;
  public percentageUploaded = 0;

  public emailControl!: FormControl;
  public isShowEmailControl = true;

  constructor(
    private router: Router,
    private fb: FormBuilder,
    private sessionApi: SessionService,
    private userApi: UsersService,
    private fileApi: FileService,
    private notificationService: NotificationService
  ) {
    this.initializeForm();
  }
  async initializeAvailableSpace() {
    const id = this.sessionApi.getStoredId();
    const userInfoStored = this.sessionApi.getStoredUserInfo();
    if (userInfoStored) {
      this.totalSpaceInBytes = userInfoStored.totalSpace;
      this.leftSpaceInBytes =
        userInfoStored.totalSpace - userInfoStored.usedSpace;
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
        // notification in interceptor
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

  initializeForm() {
    this.emailControl = this.fb.control('');
    this.uploadform = this.fb.group({
      fileFromDisk: [
        undefined,
        Validators.compose([
          Validators.required,
          fileSizeValidator(this.leftSpaceInBytes)
        ])
      ],
      emailOrLink: ['', Validators.required],
      emailMessageArray: this.fb.array([
        // this.initializeEmailMessageFormGroup()
      ]),
      linkArray: this.fb.array([
        // this.initializeLinkFormGroup()
      ]),
      expirationDate: [this.get7DaysAfterToday(), Validators.required],
      password: [undefined]
    });
  }

  getEmailMessageFormGroup(i: number): FormGroup {
    const emailMessageArray: FormArray = this.emailMessageArray;
    return <FormGroup>emailMessageArray.controls[i];
  }

  getEmailMessageFormGroupEmailsArray(i: number): FormArray {
    return <FormArray>this.getEmailMessageFormGroup(i).controls['emailArray'];
  }

  initializeEmailMessageFormGroup(): FormGroup {
    const initializedFg = this.fb.group({
      emailArray: this.fb.array([], Validators.required),
      message: ['']
    });

    return initializedFg;
  }

  initializedEmailFormGroup(): FormGroup {
    const emailGroup = this.fb.group(
      {
        email: new FormControl('', Validators.required)
      },
      { updateOn: 'change' }
    );
    return emailGroup;
  }

  initializedEmailFormGroupValue(value: any): FormGroup {
    const emailGroup = this.fb.group(
      {
        email: new FormControl(value, Validators.required)
      },
      { updateOn: 'change' }
    );
    return emailGroup;
  }

  initializeLinkFormGroup(): FormGroup {
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
    this.initializeForm();
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
  get emailMessageArray() {
    return this.uploadform.get('emailMessageArray') as FormArray;
  }

  getEmailArrayOnForm(form: any) {
    return form.controls.emailArray.controls;
  }

  addEmailFormGroup(emailMessageFormArrayIndex: number) {
    const formGroupOrNull = <FormGroup | null>(
      this.emailMessageArray.controls[emailMessageFormArrayIndex]
    );
    if (formGroupOrNull) {
      const emailArray = <FormArray | null>(
        formGroupOrNull.controls['emailArray']
      );
      if (emailArray) {
        emailArray.push(
          this.initializedEmailFormGroupValue(this.emailControl.value)
        );
        this.isShowEmailControl = false;
        this.emailControl = this.fb.control('');
        setTimeout(() => (this.isShowEmailControl = true));
      }
    }
  }

  getEmailArray(emailMessageArrayIndex: number): FormArray | null {
    const formGroupOrNull = <FormGroup | null>(
      this.emailMessageArray.controls[emailMessageArrayIndex]
    );
    if (formGroupOrNull) {
      return <FormArray | null>formGroupOrNull.controls['emailArray'];
    }
    return null;
  }

  getEmailFormGroup(
    emailMessageArrayIndex: number,
    emailArrayIndex: number
  ): FormGroup | null {
    const emailArray = this.getEmailArray(emailMessageArrayIndex);
    if (emailArray) {
      return <FormGroup | null>emailArray.controls[emailArrayIndex];
    }
    return null;
  }

  deleteEmailFormGroup(
    emailMessageArrayIndex: number,
    emailArrayIndex: number
  ) {
    const emailArray = this.getEmailArray(emailMessageArrayIndex);
    if (emailArray) {
      emailArray.removeAt(emailArrayIndex);
    }
  }

  getEmailArrayLength(emailMessageArrayIndex: number) {
    const emailArray = this.getEmailArray(emailMessageArrayIndex);
    if (emailArray) {
      return emailArray.controls.length;
    }
    return 0;
  }

  getEmailControlValue(
    emailMessageArrayIndex: number,
    emailArrayIndex: number
  ): string | null {
    const emailControl = this.getEmailControl(
      emailMessageArrayIndex,
      emailArrayIndex
    );
    if (emailControl) {
      return <string | null>emailControl.value;
    }
    return null;
  }

  getEmailControl(
    emailMessageArrayIndex: number,
    emailArrayIndex: number
  ): FormControl | null {
    const emailFormGroup = this.getEmailFormGroup(
      emailMessageArrayIndex,
      emailArrayIndex
    );
    if (emailFormGroup) {
      return <FormControl | null>emailFormGroup.controls.email;
    }
    return null;
  }

  getMessageControl(emailMessageArrayIndex: number): FormControl | null {
    const formGroupOrNull = <FormGroup | null>(
      this.emailMessageArray.controls[emailMessageArrayIndex]
    );
    if (formGroupOrNull) {
      return <FormControl | null>formGroupOrNull.controls['message'];
    }
    return null;
  }

  getMessageControlValue(emailMessageArrayIndex: number): string | null {
    const messageControl = this.getMessageControl(emailMessageArrayIndex);
    if (messageControl) {
      return <string | null>messageControl.value;
    }
    return null;
  }

  addEmailMessageFormGroup() {
    this.emailMessageArray.push(this.initializeEmailMessageFormGroup());
  }
  deleteEmailMessageFormGroup(i: number) {
    this.emailMessageArray.removeAt(i);
  }
  resetEmailMessageArray() {
    const formArray: FormArray = this.emailMessageArray;
    while (formArray.length !== 0) {
      formArray.removeAt(0);
    }
    this.emailControl = this.fb.control('');
  }

  getEmailMessageArrayLength(): number {
    const formArray: FormArray = <FormArray>this.emailMessageArray;
    if (formArray) {
      return formArray.controls.length;
    }
    return 0;
  }

  // LINKS
  get linkArray() {
    return this.uploadform.get('linkArray') as FormArray;
  }
  addLinkFormGroup() {
    this.linkArray.push(this.initializeLinkFormGroup());
  }
  deleteLinkFormGroup(i: number) {
    this.linkArray.removeAt(i);
  }
  resetLinkArray() {
    const formArray: FormArray = this.linkArray;
    while (formArray.length !== 0) {
      formArray.removeAt(0);
    }
  }
  getLinkArrayLength(): number {
    const formArray: FormArray = this.linkArray;
    if (formArray) {
      return formArray.controls.length;
    }
    return 0;
  }
  getLinkFormGroup(i: number): FormGroup | null {
    const formArray: FormArray = this.linkArray;
    if (formArray) {
      return formArray.controls[i] as FormGroup;
    }
    return null;
  }

  resetRecipients() {
    this.resetEmailMessageArray();
    this.resetLinkArray();
  }

  resetRecipientsEmail() {
    this.resetRecipients();
    this.addEmailMessageFormGroup();
  }

  resetRecipientsLink() {
    this.resetRecipients();
    this.addLinkFormGroup();
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
          for (let i = 0; i < this.getEmailMessageArrayLength(); i++) {
            const formGroupOrNull: FormGroup | null = this.getEmailMessageFormGroup(
              i
            );
            if (formGroupOrNull) {
              const messageOrNull: string | null = this.getMessageControlValue(
                i
              );
              for (let j = 0; j < this.getEmailArrayLength(i); j++) {
                const emailOrNull: string | null = this.getEmailControlValue(
                  i,
                  j
                );
                if (emailOrNull && emailOrNull !== '') {
                  const recipient: Recipient = {
                    emailOrName: emailOrNull,
                    sendEmail: this.emailOrLinkIsEmail()
                  };
                  if (
                    messageOrNull &&
                    messageOrNull !== '' &&
                    this.emailOrLinkIsEmail
                  ) {
                    recipient.message = messageOrNull;
                  }
                  recipientArray.push(recipient);
                }
              }
            }
          }
        } else {
          // IS LINK
          for (let i = 0; i < this.getLinkArrayLength(); i++) {
            const formGroupOrNull = this.getLinkFormGroup(i);
            if (formGroupOrNull) {
              const name: string = formGroupOrNull.controls['name'].value;
              const recipient: Recipient = {
                emailOrName: name,
                sendEmail: this.emailOrLinkIsEmail()
              };
              recipientArray.push(recipient);
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
        const fileInfoUploader = await this.fileApi
          .postFileContent(fileId, this.getFileFromDisk(), 'events', true)
          .pipe(map(event => this.getEventMessage(event)))
          .toPromise();

        if (fileInfoUploader) {
          this.router.navigateByUrl('uploadSuccess', {
            state: { data: fileInfoUploader }
          });
        }
        await this.initializeAvailableSpace();
      } catch (e) {
        // notification in interceptor
      }
    }
    this.uploadInProgress = false;
    this.initializeForm();
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
          this.uploadInProgress = false;
          this.percentageUploaded = 0;
          return event.body as FileInfoUploader;
        } else {
          // notification sent in interceptor
        }
        this.uploadInProgress = false;
        this.percentageUploaded = 0;
        return;

      case HttpEventType.ResponseHeader:
        this.uploadInProgress = false;
        this.percentageUploaded = 0;
        // notification sent in interceptor
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
