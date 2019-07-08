/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { messageToRecipientValidator, sourceValidator, customFileValidator, globalValidator } from './upload.validators';
import { Component, OnInit, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { FormArray, ReactiveFormsModule, FormGroup, Validators, ValidatorFn, AbstractControl, FormBuilder, ValidationErrors, FormControl, NgForm } from '@angular/forms';
import { faUpload } from '@fortawesome/free-solid-svg-icons';
import { CircabcService } from '../service/circabc.service';
import { InterestGroup } from '../interfaces/interest-group';
import { FolderInfo } from '../interfaces/folder-info';
import { FileService, FileRequest, Recipient, UsersService, SessionService } from '../openapi';
import { NotificationService } from '../common/notification/notification.service';
import { environment } from '../../environments/environment'
import { routerNgProbeToken } from '@angular/router/src/router_module';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css']
})
export class UploadComponent implements OnInit {

  public faUpload = faUpload;
  public modalClass = 'modal';
  public selectImportOptions: Array<NameAndValue> = [];
  public selectIGoptions?: InterestGroup[];
  public selectedIGFolders?: FolderInfo[];
  public moreOptions = false;
  public uploadInProgress = false;
  public uploadform!: FormGroup;
  private formBuilder: FormBuilder;
  public shareWithUser = '';
  public selectedFolder?: FolderInfo;
  private leftSpaceInBytes = 0;
  private emailRegex = '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,4}$';
  private submited = false;

  constructor(private fb: FormBuilder, private sessionApi: SessionService, private userApi: UsersService, private fileApi: FileService, private circabc: CircabcService, private notificationService: NotificationService) {
    this.formBuilder = fb;
  }

  initializeAvailableSpace() {
    const id = this.sessionApi.getStoredId();
    if (id) {
      this.userApi.getUserUserInfo(id).toPromise().then((me) => {
        const leftSpaceInBytes = me.totalSpace - me.usedSpace;
        if (leftSpaceInBytes > 0) {
          this.leftSpaceInBytes = leftSpaceInBytes;
        }
        this.leftSpaceInBytes = 0;
      }).catch(error => {
        this.notificationService.addErrorMessage('A problem occured while retrieving your user informations');
      });
    }
  }

  initializeAvailableIGs() {
    const id = this.sessionApi.getStoredId();
    if (id) {
      this.userApi.getUserUserInfo(id).toPromise().then((me) => {
        this.circabc.getUserMembership(me.id).then((memberShip => {
          this.selectIGoptions = memberShip.interestGroups;
        })).catch(error => {
          this.notificationService.addErrorMessage('A problem occured while retrieving your interest groups\' memberships');
        });
      }).catch(error => {
        this.notificationService.addErrorMessage('A problem occured while retrieving your interest groups\' memberships');
      });
    }
  }

  initializeEventListeners() {
    window.addEventListener('dragover', e => {
      e.preventDefault();
    }, false);
    window.addEventListener('drop', e => {
      e.preventDefault();
    }, false);
  }

  initializeSelectImportOption() {
    const selectNames: Array<string> = Object.values(SelectImportEnum);
    const selectValues: Array<string> = Object.keys(SelectImportEnum);
    this.selectImportOptions = new Array<NameAndValue>();
    for (let i = 0; i < selectNames.length; i++) {
      this.selectImportOptions.push(new NameAndValue(selectNames[i], selectValues[i]));
    }
  }

  initializeFormGroup() {
    this.uploadform = this.formBuilder.group({
      selectImport: ['DISK', Validators.compose([Validators.required, sourceValidator(Object.keys(SelectImportEnum))])],
      fileFromDisk: [undefined, Validators.compose([customFileValidator(this.leftSpaceInBytes)])],
      selectInterestGroupID: [undefined],
      emailOrLink: ['', Validators.required],
      emailsWithMessages: this.formBuilder.array([
        this.initializedEmailsWithMessages()
      ]),
      namesOnly: this.formBuilder.array([
        this.initializeNamesOnly()
      ]),
      expirationDate: [this.get7DaysAfterToday(), Validators.required],
      password: [undefined]
    }, { validators: (globalValidator()) });
  }

  initializedEmailsWithMessages(): FormGroup {
    return this.formBuilder.group({
      email: new FormControl('', Validators.compose([
        Validators.required,
        Validators.pattern(this.emailRegex)])),
      message: ['', Validators.compose([messageToRecipientValidator(400)])]
    }, { updateOn: 'blur' });
  }

  initializeNamesOnly(): FormGroup {
    return this.formBuilder.group({
      name: new FormControl('', Validators.required)
    }, { updateOn: 'blur' });
  }


  ngOnInit() {
    this.initializeEventListeners();
    this.initializeSelectImportOption();
    this.initializeAvailableSpace();
    this.initializeAvailableIGs();
    this.initializeFormGroup();
  }

  // SELECT IMPORT
  getSelectImport(): string {
    return this.uploadform.controls['selectImport'].value;
  }
  selectImportIsDisk(): boolean {
    if (this.getSelectImport() === 'DISK') {
      this.resetSelectInterestGroupID();
      return true;
    } else {
      this.resetFileFromDisk();
      return false;
    }
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

  // SELECT INTEREST GROUP
  getSelectInterestGroupID(): string {
    return this.uploadform.controls['selectInterestGroupID'].value;
  }
  resetSelectInterestGroupID(): void {
    this.uploadform.controls['selectInterestGroupID'].reset();
  }
  getIGFromID(igID: string): InterestGroup | undefined {
    if (this.selectIGoptions) {
      for (const ig of this.selectIGoptions) {
        if (ig.id === igID) {
          return ig;
        }
      }
      return undefined;
    }
  }
  getSelectInterestGroup(): InterestGroup | undefined {
    return this.getIGFromID(this.getSelectInterestGroupID());
  }

  // EMAIL OR LINK
  getEmailOrLink(): string {
    return this.uploadform.controls['emailOrLink'].value;
  }
  emailOrLinkIsEmail(): boolean {
    return (this.getEmailOrLink() === 'Email');
  }

  emailOrLinkIsLink(): boolean {
    return (this.getEmailOrLink() === 'Link');
  }

  // FOLDERS AND FILES
  updateSelectedIGFolders(): void {
    const ig = this.getIGFromID(this.getSelectInterestGroupID());
    if (ig) {
      this.circabc.getInterestGroupFolders(ig).then(folders => {
        this.selectedIGFolders = folders;
      });
    }
  }

  onClickFolder(fol: FolderInfo) {
    this.selectedFolder = fol;
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
    const formArray: FormArray = <FormArray>this.uploadform.controls['emailsWithMessages'];
    if (formArray) {
      return formArray.controls.length;
    }
    return 0;
  }
  getEmailsWithMessagesFormgroup(i: number): FormGroup | null {
    const formArray: FormArray = <FormArray>this.uploadform.controls['emailsWithMessages'];
    if (formArray) {
      return <FormGroup>formArray.controls[i];
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
    const formArray: FormArray = <FormArray>this.uploadform.controls['namesOnly'];
    if (formArray) {
      return formArray.controls.length;
    }
    return 0;
  }

  getNamesOnlyFormgroup(i: number): FormGroup | null {
    const formArray: FormArray = <FormArray>this.uploadform.controls['namesOnly'];
    if (formArray) {
      return <FormGroup>formArray.controls[i];
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
    this.uploadform.controls['expirationDate'].setValue(this.get7DaysAfterToday());
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
        let recipientArray = new Array<Recipient>();
        if (this.emailOrLinkIsEmail()) {
          for (let i = 0; i < this.getEmailsWithMessagesFormgroupNumber(); i++) {
            const message: string = this.getEmailsWithMessagesFormgroup(i)!.controls['message'].value;
            const email: string = this.getEmailsWithMessagesFormgroup(i)!.controls['email'].value;
            let recipient: Recipient = {
              emailOrName: email,
              sendEmail: this.emailOrLinkIsEmail()
            }
            if (message && message != "" && this.emailOrLinkIsEmail) {
              recipient.message = message;
            }
            recipientArray.push(recipient);
          }
        } else {
          for (let i = 0; i < this.getNamesOnlyFormgroupNumber(); i++) {
            const name: string = this.getNamesOnlyFormgroup(i)!.controls['name'].value;
            let recipient: Recipient = {
              emailOrName: name,
              sendEmail: this.emailOrLinkIsEmail()
            }
            recipientArray.push(recipient);
          }
        }
        let myFileRequest: FileRequest = {
          expirationDate: this.getExpirationDate().toISOString().substring(0, 10),
          hasPassword: (this.getPassword() != null && this.getPassword() !== ""),
          name: this.getFileFromDisk().name,
          size: this.getFileFromDisk().size,
          sharedWith: recipientArray
        };
        if (this.getPassword() !== "") {
          myFileRequest.password = this.getPassword();
        }
        const fileId = await this.fileApi.postFileFileRequest(myFileRequest).toPromise();
        await this.fileApi.postFileContent(fileId, this.getFileFromDisk()).toPromise();


        if (this.emailOrLinkIsEmail()) {
          this.notificationService.addSuccessMessage('Your recipients have been notified by mail that they may download the shared file!', false);
        } else {
          this.notificationService.addSuccessMessage('Please find for each of your recipients, a personnal download link on the My Shared Files page', false);
          //this.notificationService.addSuccessMessage('Please share the following link with your recipients: ' + environment.frontend_url + '/filelink/' + fileId +'/' + btoa(myFileRequest.name), false);
        }
      } catch (e) {
        this.notificationService.addErrorMessage('A problem occured while uploading your file, please try again later or contact the support', false);
        this.uploadInProgress = false;
        return;
      }
    }
    this.uploadInProgress = false;
    this.initializeFormGroup();
    this.notificationService.addSuccessMessage('Your upload was successful!', true);
  }

  toggleModal() {
    if (this.modalClass.endsWith(' is-active')) {
      // deactivating
      this.modalClass = this.modalClass.replace(' is-active', '');
      this.selectedFolder = undefined;
    } else {
      // activating
      this.modalClass = this.modalClass.concat(' is-active');
      this.updateSelectedIGFolders();
    }
  }
  get uf() { return this.uploadform.controls; }

  getFormValidationErrors() {
    Object.keys(this.uploadform.controls).forEach(key => {
      const controlErrors: ValidationErrors | null = this.uploadform.get(key)!.errors;
      if (controlErrors != null) {
        Object.keys(controlErrors).forEach(keyError => {
          console.log('Key control: ' + key + ', keyError: ' + keyError + ', err value: ', controlErrors[keyError]);
        });
      }
    });
  }
}


export enum SelectImportEnum {
  DISK = 'Disk',
  IG = 'Interest group'
}

export enum SelectSendOptionsEnum {
  EMAIL = 'Email',
  LINK = 'Link'
}

export class NameAndValue {
  public displayedValue: string;
  public displayedName: string;

  constructor(displayedName: string, displayedValue: string) {
    this.displayedName = displayedName;
    this.displayedValue = displayedValue;
  }
}
