/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { RecipientWithLink } from '../../openapi';

@Injectable({
  providedIn: 'root'
})
export class ModalsService {
  private possibleActiveModals: string[] = [
    'download',
    'fileLink',
    'addRecipients',
    'shareWithUsers',
    ' '
  ];
  private activeModal!: string;

  private activateDownloadModalSubject = new Subject<DownloadModalValue>();
  public activateDownloadModal$: Observable<
    DownloadModalValue
  > = this.activateDownloadModalSubject.asObservable();

  private activateFileLinkModalSubject = new Subject<FileLinkModalValue>();
  public activateFileLinkModal$: Observable<
    FileLinkModalValue
  > = this.activateFileLinkModalSubject.asObservable();

  private activateAddRecipientsModalSubject = new Subject<
    AddRecipientsModalValue
  >();
  public activateAddRecipientsModal$: Observable<
    AddRecipientsModalValue
  > = this.activateAddRecipientsModalSubject.asObservable();

  private activateShareWithUsersModalSubject = new Subject<
    ShareWithUsersModalValue
  >();
  public activateShareWithUsersModal$: Observable<
    ShareWithUsersModalValue
  > = this.activateShareWithUsersModalSubject.asObservable();

  constructor() {}

  public activateShareWithUsersModal(
    modalFileName: string,
    modalFileId: string,
    recipientsWithLink: RecipientWithLink[],
    modalFileHasPassword: boolean
  ) {
    if (this.activeModal && this.activeModal !== this.possibleActiveModals[4]) {
      this.deactivateAllModals();
    }
    this.activeModal = this.possibleActiveModals[3];
    this.activateShareWithUsersModalSubject.next({
      modalActive: true,
      modalFileName: modalFileName,
      modalFileId: modalFileId,
      modalFileHasPassword: modalFileHasPassword,
      recipientsWithLink: recipientsWithLink
    });
  }

  public activateAddRecipientsModal(
    modalFileName: string,
    modalFileId: string
  ) {
    if (this.activeModal && this.activeModal !== this.possibleActiveModals[2]) {
      this.deactivateAllModals();
    }
    this.activeModal = this.possibleActiveModals[2];
    this.activateAddRecipientsModalSubject.next({
      modalActive: true,
      modalFileName: modalFileName,
      modalFileId: modalFileId
    });
  }

  public activateFileLinkModal(link: string) {
    if (this.activeModal && this.activeModal !== this.possibleActiveModals[1]) {
      this.deactivateAllModals();
    }
    this.activeModal = this.possibleActiveModals[1];
    this.activateFileLinkModalSubject.next({
      modalActive: true,
      fileLink: link
    });
  }

  public activateDownloadModal(
    modalFileId: string,
    modalFileName: string,
    modalFileHasPassword: boolean
  ) {
    if (this.activeModal && this.activeModal !== this.possibleActiveModals[0]) {
      this.deactivateAllModals();
    }
    this.activeModal = this.possibleActiveModals[0];
    this.activateDownloadModalSubject.next({
      modalActive: true,
      modalFileId: modalFileId,
      modalFileName: modalFileName,
      modalFileHasPassword: modalFileHasPassword
    });
  }

  public deactivateDownloadModal() {
    if (this.activeModal && this.activeModal === this.possibleActiveModals[0]) {
      this.activeModal = ' ';
      this.activateDownloadModalSubject.next({
        modalActive: false,
        modalFileId: '',
        modalFileName: '',
        modalFileHasPassword: false
      });
    }
  }

  public deactivateFileLinkModal() {
    if (this.activeModal && this.activeModal === this.possibleActiveModals[1]) {
      this.activeModal = ' ';
      this.activateFileLinkModalSubject.next({
        modalActive: false,
        fileLink: ''
      });
    }
  }

  public deactivateAddRecipientsModal() {
    if (this.activeModal && this.activeModal === this.possibleActiveModals[2]) {
      this.activeModal = ' ';
      this.activateAddRecipientsModalSubject.next({
        modalActive: false,
        modalFileName: '',
        modalFileId: ''
      });
    }
  }

  public deactivateShareWithUsersModal() {
    if (this.activeModal && this.activeModal === this.possibleActiveModals[3]) {
      this.activeModal = ' ';
      this.activateShareWithUsersModalSubject.next({
        modalActive: false,
        modalFileName: '',
        modalFileId: '',
        modalFileHasPassword: false,
        recipientsWithLink: []
      });
    }
  }

  private deactivateAllModals() {
    this.deactivateDownloadModal();
    this.deactivateFileLinkModal();
    this.deactivateAddRecipientsModal();
  }
}
export interface DownloadModalValue {
  modalActive: boolean;
  modalFileId: string;
  modalFileName: string;
  modalFileHasPassword: boolean;
}

export interface FileLinkModalValue {
  modalActive: boolean;
  fileLink: string;
}

export interface AddRecipientsModalValue {
  modalActive: boolean;
  modalFileName: string;
  modalFileId: string;
}

export interface ShareWithUsersModalValue {
  modalActive: boolean;
  modalFileName: string;
  modalFileId: string;
  modalFileHasPassword: boolean;
  recipientsWithLink: RecipientWithLink[];
}
