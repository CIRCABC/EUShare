/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { messageToRecipientValidator, sourceValidator, customFileValidator, globalValidator } from './upload.validators';
import { Component, OnInit } from '@angular/core';
import { FormArray, ReactiveFormsModule, FormGroup, Validators, ValidatorFn, AbstractControl, FormBuilder } from '@angular/forms';
import { faUpload } from '@fortawesome/free-solid-svg-icons';
import { ApiService } from '../service/api.service';
import { CircabcService } from '../service/circabc.service';
import { InterestGroup } from '../interfaces/interest-group';
import { fbind } from 'q';
import { FolderInfo } from '../interfaces/folder-info';
import { CalendarModule } from 'primeng/calendar';
import { MeService, FileService, FileRequest, Recipient } from '../openapi';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css']
})
export class UploadComponent implements OnInit {
  selectImportOptions: Array<NameAndValue> = [];
  faUpload = faUpload;
  selectIGoptions?: InterestGroup[];
  selectedIGFolders?: FolderInfo[];
  selectedFolder?: FolderInfo;
  moreOptions = false;
  uploadInProgress = false;

  private leftSpaceInBytes = 0;
  public uploadform!: FormGroup;
  private formBuilder: FormBuilder;

  public shareWithUser = '';

  modalClass = 'modal';
  submited = false;

  constructor(private fb: FormBuilder, private meApi: MeService, private fileApi: FileService, private circabc: CircabcService) {
    this.formBuilder = fb;
  }

  initializeAvailableSpace() {
    this.meApi.getUserInfo().toPromise().then((me) => {
      const leftSpaceInBytes = me.totalSpace - me.usedSpace;
      if (leftSpaceInBytes > 0) {
        this.leftSpaceInBytes = leftSpaceInBytes;
      }
      this.leftSpaceInBytes = 0;
    });
  }

  initializeAvailableIGs() {
    this.meApi.getUserInfo().toPromise().then((userInfo => {
      console.log('Retrieving memberships for ' + userInfo.id);
      this.circabc.getUserMembership(userInfo.id).then((memberShip => {
        this.selectIGoptions = memberShip.interestGroups;
      }));
    }));
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
      fileFromDisk: [undefined, Validators.compose([customFileValidator(209715200)])],
      selectInterestGroupID: [undefined],
      emailOrLink: ['', Validators.required],
      emailsWithMessages: this.formBuilder.array([
        this.initializedEmailsWithMessages()
      ]),
      expirationDate: [this.get7DaysAfterToday(), Validators.required],
      password: [undefined]
    }, { validators: (globalValidator()) });
  }

  initializedEmailsWithMessages(): FormGroup {
    return this.formBuilder.group({
      email: ['', Validators.compose([Validators.required, Validators.pattern('^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$')])],
      message: ['', Validators.compose([messageToRecipientValidator(20)])]
    });
  }

  ngOnInit() {
    console.log('ngOnInit');
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
    formArray.push(this.initializedEmailsWithMessages());
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
    const formGroup = this.getEmailsWithMessagesFormgroup(i);
    if (formGroup) {
      return formGroup.controls['message'];
    }
    return null;
  }

  getEmailsWithMessagesOnlyEmail(i: number): AbstractControl | null {
    const formGroup = this.getEmailsWithMessagesFormgroup(i);
    if (formGroup) {
      return formGroup.controls['email'];
    }
    return null;
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
    console.log('submit!');
    this.uploadInProgress = true;

    const recipients = Array<Recipient>();

    for (let _i = 0; _i < this.getEmailsWithMessagesFormgroupNumber(); _i++) {
      const item = this.getEmailsWithMessagesOnlyEmail(_i);
      if (item) {
        recipients.push({ emailOrID: item.value, message: '' });
      }
    }

    if (this.getFileFromDisk()) {
      try {
        let fileRequest: FileRequest;
        let recipientArray = new Array<Recipient>();
        for (let i = 0; i < this.getEmailsWithMessagesFormgroupNumber(); i++) {
          const message: string = this.getEmailsWithMessagesFormgroup(i)!.controls['message'].value;
          const email: string = this.getEmailsWithMessagesFormgroup(i)!.controls['email'].value;
          console.log(email + i);
          console.log(message + i);
          if (email) {
            recipientArray.push({ emailOrID: email, message: message }
            )
          } else {
            // modal error
          }
        }
        let myFileRequest : FileRequest = {
          expirationDate: this.getExpirationDate().toISOString().substring(0,10),
          hasPassword: (this.getPassword() != null && this.getPassword() !== ""),
          name: this.getFileFromDisk().name,
          size: this.getFileFromDisk().size,
          sharedWith: recipientArray
          };
        if(this.getPassword()!=="") {
          myFileRequest.password = this.getPassword();
        }
        this.fileApi.postFileFileRequest(myFileRequest).subscribe(value => {
          console.log('fileId = '+ value);
          this.fileApi.postFileContent(value, this.getFileFromDisk()).subscribe(value =>{
            console.log(value);
          })
        });
      } catch (e) {
        console.error(e);
      }
    }

    this.uploadInProgress = false;
  }

  toggleModal() {
    console.log(this.getSelectInterestGroupID());
    console.log(typeof this.getSelectInterestGroupID());
    console.log(this.getIGFromID(this.getSelectInterestGroupID()));

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
