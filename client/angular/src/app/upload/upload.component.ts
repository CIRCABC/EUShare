/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { HttpEvent, HttpEventType } from '@angular/common/http';
import {
  AfterViewInit,
  Component,
  OnInit,
  Renderer2,
  ViewChild,
} from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { faUpload, faUserSlash } from '@fortawesome/free-solid-svg-icons';
import { firstValueFrom } from 'rxjs';
import { map } from 'rxjs/operators';
import { ModalsService } from '../common/modals/modals.service';
import { NotificationService } from '../common/notification/notification.service';
import { FileSizeFormatPipe } from '../common/pipes/file-size-format.pipe';
import { fileSizeValidator } from '../common/validators/file-validator';
import {
  FileInfoUploader,
  FileRequest,
  FileService,
  Recipient,
  UserInfo,
  UsersService,
} from '../openapi';
import { SessionStorageService } from '../services/session-storage.service';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslocoModule, getBrowserLang } from '@ngneat/transloco';
import { EmailInputComponent } from '../common/formComponents/email-input/email-input.component';
import { MessageTextAreaComponent } from '../common/formComponents/message-text-area/message-text-area.component';
import { FileAccessorDirective } from '../directives/file-accessor.directive';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { I18nService } from '../common/i18n/i18n.service';
import { UploadRightsDialogComponent } from '../common/dialogs/upload-rights-dialog/upload-rights-dialog.component';
import { CaptchaComponent } from '../common/captcha/captcha.component';
import { MatTooltipModule } from '@angular/material/tooltip';
import { environment } from '../../environments/environment';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.scss'],
  standalone: true,
  imports: [
    ReactiveFormsModule,
    FileAccessorDirective,
    FontAwesomeModule,
    EmailInputComponent,
    MessageTextAreaComponent,
    FileSizeFormatPipe,
    TranslocoModule,
    MatDialogModule,
    MatSnackBarModule,
    CaptchaComponent,
    MatTooltipModule,
    MatIconModule,
  ],
  providers: [FileSizeFormatPipe],
})
export class UploadComponent implements OnInit, AfterViewInit {
  public faUserSlash = faUserSlash;
  public faUpload = faUpload;
  public moreOptions = false;
  public uploadInProgress = false;
  public uploadform!: FormGroup;
  public shareWithUser = '';
  public leftSpaceInBytes = 0;
  public totalSpaceInBytes = 0;
  public percentageUploaded = 0;
  public userRole = 'EXTERNAL';
  public useCaptcha = false;
  public isLoading = true;

  public emailControl!: FormControl;
  public isShowEmailControl = true;
  public acceptTos = false;
  public circabc_url: string = environment.circabc_url;

  @ViewChild(CaptchaComponent)
  private captchaComponent!: CaptchaComponent;

  constructor(
    private router: Router,
    private fb: FormBuilder,
    private sessionApi: SessionStorageService,
    private userApi: UsersService,
    private fileApi: FileService,
    private notificationService: NotificationService,
    private i18nService: I18nService,
    private modalService: ModalsService,
    private fileSizePipe: FileSizeFormatPipe,
    private renderer: Renderer2,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
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
        const me = await firstValueFrom(this.userApi.getUserUserInfo(id));
        if (me.role) this.userRole = me.role;

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

  async ngOnInit() {
    const id = this.sessionApi.getStoredId();
    if (id) {
      try {
        const me = await firstValueFrom(this.userApi.getUserUserInfo(id));
        if (me.role) this.userRole = me.role;

        this.useCaptcha = this.userRole === UserInfo.RoleEnum.External;
        this.isLoading = false;
      } catch (error) {
        // notification in interceptor
      }
    }

    this.initializeEventListeners();
    await this.initializeAvailableSpace();
    this.initializeForm();
  }

  ngAfterViewInit() {
    // Get a reference to the file input element
    const fileInput = document.getElementById('fileFromDisk');

    // Register the click event listener using the Renderer2 service
    this.renderer.listen(fileInput, 'change', (event) => {
      this.checkExistingFile(event.target.files[0]);
    });

    if (this.useCaptcha) {
      const activeLang = getBrowserLang();
      if (
        this.captchaComponent !== undefined &&
        this.captchaComponent.answer !== undefined
      ) {
        this.captchaComponent.answer.setValue('');
        if (activeLang && activeLang !== this.captchaComponent.languageCode) {
          this.captchaComponent.languageCode = activeLang;
        }
      }
    }
  }

  initializeEventListeners() {
    window.addEventListener(
      'dragover',
      (e) => {
        e.preventDefault();
      },
      false
    ); // NOSONAR
    window.addEventListener(
      'drop',
      (e) => {
        e.preventDefault();
      },
      false
    ); // NOSONAR
  }

  initializeForm() {
    this.emailControl = this.fb.nonNullable.control('');

    const message = this.i18nService.translate('file.size.bigger.quota', {
      fileSizeMax: this.fileSizePipe.transform(this.leftSpaceInBytes),
    });

    this.uploadform = this.fb.nonNullable.group({
      fileFromDisk: [
        undefined,
        Validators.compose([
          Validators.required,
          fileSizeValidator(
            this.leftSpaceInBytes,
            this.notificationService,
            message
          ),
        ]),
      ],
      emailMessageArray: this.fb.nonNullable.array([
        // this.initializeEmailMessageFormGroup()
      ]),
      linkArray: this.fb.nonNullable.array([
        // this.initializeLinkFormGroup()
      ]),
      acceptTos: [false],
      expirationDate: [this.get7DaysAfterToday(), Validators.required],
      password: [undefined],
      downloadNotification: [false],
    });
    this.resetEmailMessageArray();
    this.addEmailMessageFormGroup();
  }

  getEmailMessageFormGroup(i: number): FormGroup {
    const emailMessageArray: FormArray = this.emailMessageArray;
    return <FormGroup>emailMessageArray.controls[i];
  }

  getEmailMessageFormGroupEmailsArray(i: number): FormArray {
    return <FormArray>this.getEmailMessageFormGroup(i).controls['emailArray'];
  }

  initializeEmailMessageFormGroup(): FormGroup {
    return this.fb.nonNullable.group({
      emailArray: this.fb.nonNullable.array([]),
      message: [''],
    });
  }

  initializedEmailFormGroup(): FormGroup {
    return this.fb.nonNullable.group(
      {
        email: new FormControl('', Validators.required),
      },
      { updateOn: 'change' }
    );
  }

  initializedEmailFormGroupValue(value: any): FormGroup {
    return this.fb.nonNullable.group(
      {
        email: new FormControl(value),
      },
      { updateOn: 'change' }
    );
  }

  public lastfile!: File;

  checkExistingFile(file: File) {
    if (this.lastfile && file !== this.lastfile) {
      this.modalService.activateOverwriteConfirmModal(
        file.name,
        this.lastfile.name
      );
    }
    this.lastfile = file;
  }

  isUserExternal() {
    return this.sessionApi.getStoredUserInfo()?.role?.toString() === 'EXTERNAL';
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
    this.checkExistingFile(file);
    this.uploadform.controls['fileFromDisk'].setValue(file);
  }
  resetFileFromDisk(): void {
    this.uploadform.controls['fileFromDisk'].reset();
  }

  // EMAIL WITH MESSAGES
  get emailMessageArray() {
    return this.uploadform.get('emailMessageArray') as FormArray;
  }

  getEmailArrayOnForm(form: any) {
    return form.controls.emailArray.controls;
  }

  addEmailFormGroup(emailMessageFormArrayIndex: number) {
    if (
      this.userRole === 'EXTERNAL' &&
      this.getEmailArrayLength(emailMessageFormArrayIndex) >= 5
    ) {
      this.notificationService.addWarningMessageTranslation(
        'max.recepient.reached'
      );
      return;
    }
    if (
      this.userRole === 'TRUSTED_EXTERNAL' &&
      this.getEmailMessageArrayLength() >= 25
    ) {
      this.notificationService.addWarningMessageTranslation(
        'max.recepient.reached'
      );
      return;
    }
    const formGroupOrNull = <FormGroup | null>(
      this.emailMessageArray.controls[emailMessageFormArrayIndex]
    );
    if (formGroupOrNull) {
      const emailArray = <FormArray | null>(
        formGroupOrNull.controls['emailArray']
      );
      if (emailArray) {
        const addEmail = this.initializedEmailFormGroupValue(
          this.emailControl.value
        );
        const emailAlreadyExist = emailArray.controls.some(
          (element) =>
            element.value.email.toUpperCase() ===
            addEmail.controls['email'].value.toUpperCase()
        );
        if (!emailAlreadyExist && addEmail.controls['email'].value) {
          emailArray.push(addEmail);
        }
        this.isShowEmailControl = false;
        this.emailControl = this.fb.nonNullable.control('');
        setTimeout(() => {
          this.isShowEmailControl = true;
        });
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
      return <FormControl | null>emailFormGroup.controls['email'];
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
    this.emailControl = this.fb.nonNullable.control('');
  }

  getEmailMessageArrayLength(): number {
    const formArray: FormArray = this.emailMessageArray;
    if (formArray) {
      return formArray.controls.length;
    }
    return 0;
  }

  // MORE OPTIONS
  toggleMoreOptions() {
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
  getDownloadNotification(): boolean {
    return this.uploadform.controls['downloadNotification'].value;
  }
  resetPassword(): void {
    this.uploadform.controls['password'].reset();
  }

  async submit() {
    this.uploadInProgress = true;

    if (this.getFileFromDisk()) {
      try {
        const recipientArray = new Array<Recipient>();

        for (let i = 0; i < this.getEmailMessageArrayLength(); i++) {
          const formGroupOrNull: FormGroup | null =
            this.getEmailMessageFormGroup(i);
          if (formGroupOrNull) {
            const messageOrNull: string | null = this.getMessageControlValue(i);
            for (let j = 0; j < this.getEmailArrayLength(i); j++) {
              const emailOrNull: string | null = this.getEmailControlValue(
                i,
                j
              );
              if (emailOrNull && emailOrNull !== '') {
                const recipient: Recipient = {
                  email: emailOrNull,
                };
                if (messageOrNull && messageOrNull !== '') {
                  recipient.message = messageOrNull;
                }
                recipientArray.push(recipient);
              }
            }
          }
        }
        if (this.getEmailArrayLength(0) === 0) {
          const recipient: Recipient = {
            email: '',
          };
          recipientArray.push(recipient);
        }

        const myFileRequest: FileRequest = {
          expirationDate: this.getExpirationDate()
            .toISOString()
            .substring(0, 10),
          hasPassword: this.getPassword() !== null && this.getPassword() !== '',
          name: this.getFileFromDisk().name,
          size: this.getFileFromDisk().size,
          sharedWith: recipientArray,
          downloadNotification: this.getDownloadNotification(),
        };
        if (this.getPassword() !== '') {
          myFileRequest.password = this.getPassword();
        }

        const fileResult = await firstValueFrom(
          this.fileApi.postFileFileRequest(
            myFileRequest,
            ...(this.useCaptcha
              ? [
                  this.captchaComponent.captchaId,
                  this.captchaComponent.captchaToken,
                  this.captchaComponent.answer.value as string,
                ]
              : [])
          )
        );

        // do not use firstValueFrom bellow, because it does not work
        const fileInfoUploader = await this.fileApi
          .postFileContent(
            fileResult.fileId,
            this.getFileFromDisk(),
            'events',
            true
          )
          .pipe(map((event) => this.getEventMessage(event)))
          .toPromise(); // NOSONAR

        if (fileInfoUploader) {
          this.router.navigateByUrl('uploadSuccess', {
            state: { data: fileInfoUploader },
          });
        }
        await this.initializeAvailableSpace();
      } catch (e) {
        // notification in interceptor
      }
    }
    this.uploadInProgress = false;
    if (this.useCaptcha) {
      if (!this.captchaComponent.answer) this.initializeForm();
      this.refreshCaptcha();
    }
  }

  get uf() {
    return this.uploadform.controls;
  }

  private getEventMessage(event: HttpEvent<any>) {
    switch (event.type) {
      case HttpEventType.Sent:
        return;

      case HttpEventType.UploadProgress: {
        let eventTotalOrUndefined = event.total;
        if (eventTotalOrUndefined === undefined) {
          eventTotalOrUndefined = 1;
        }
        const percentDone = Math.round(
          (event.loaded * 100) / eventTotalOrUndefined
        );
        this.percentageUploaded = percentDone === 100 ? 99 : percentDone;
        return;
      }

      case HttpEventType.Response: {
        if (event.status === 200) {
          this.uploadInProgress = false;
          this.percentageUploaded = 0;
          return event.body as FileInfoUploader;
        }
        this.uploadInProgress = false;
        this.percentageUploaded = 0;
        return;
      }

      case HttpEventType.ResponseHeader: {
        this.uploadInProgress = false;
        this.percentageUploaded = 0;
        // notification sent in interceptor
        return;
      }

      case HttpEventType.DownloadProgress:
        return;

      default:
        this.notificationService.addErrorMessage(
          `${this.i18nService.translate(
            'error.occurred.download'
          )} ${this.i18nService.contactSupport()} ${JSON.stringify(event)}`
        );
        this.uploadInProgress = false;
        this.percentageUploaded = 0;
        return;
    }
  }

  public openTrustDialog(): void {
    this.dialog.open(UploadRightsDialogComponent, {
      data: {},
    });
  }

  public isCaptchaInValid(): boolean {
    if (this.captchaComponent?.answer) {
      return this.captchaComponent.answer.invalid;
    }
    return true;
  }

  refreshCaptcha(): Promise<void> {
    return this.captchaComponent
      .refresh()
      .then(() => {})
      .catch((error) => {
        console.error('Error refreshing the captcha:', error);
      });
  }
}
