/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { FileLog, Recipient } from '../../openapi';

@Injectable({
  providedIn: 'root',
})
export class ModalsService {
  private possibleActiveModals: string[] = [
    'download',
    'fileLink',
    'addRecipients',
    'shareWithUsers',
    'deleteConfirm',
    'statistics',
    'changeExpirationDate',
  ];
  private activeModal!: string;

  private activateDownloadModalSubject = new Subject<DownloadModalValue>();
  public activateDownloadModal$: Observable<DownloadModalValue> =
    this.activateDownloadModalSubject.asObservable();

  private activateFileLinkModalSubject = new Subject<FileLinkModalValue>();
  public activateFileLinkModal$: Observable<FileLinkModalValue> =
    this.activateFileLinkModalSubject.asObservable();

  private activateAddRecipientsModalSubject =
    new Subject<AddRecipientsModalValue>();
  public activateAddRecipientsModal$: Observable<AddRecipientsModalValue> =
    this.activateAddRecipientsModalSubject.asObservable();

  private activateShareWithUsersModalSubject =
    new Subject<ShareWithUsersModalValue>();
  public activateShareWithUsersModal$: Observable<ShareWithUsersModalValue> =
    this.activateShareWithUsersModalSubject.asObservable();

  private activateStatisticsModalSubject = new Subject<StatisticsModalValue>();
  public activateStatisticsModal$: Observable<StatisticsModalValue> =
    this.activateStatisticsModalSubject.asObservable();

  private activateDeleteConfirmModalSubject =
    new Subject<DeleteConfirmModalValue>();
  public activateDeleteConfirmModal$: Observable<DeleteConfirmModalValue> =
    this.activateDeleteConfirmModalSubject.asObservable();

  private activateChangeExpirationDateModalSubject =
    new Subject<ChangeExpirationDateModalValue>();
  public activateChangeExpirationDateModal$: Observable<ChangeExpirationDateModalValue> =
    this.activateChangeExpirationDateModalSubject.asObservable();

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
      modalFileId,
      modalFileName,
      modalFileHasPassword,
    });
  }

  public activateFileLinkModal(link: string) {
    if (this.activeModal && this.activeModal !== this.possibleActiveModals[1]) {
      this.deactivateAllModals();
    }
    this.activeModal = this.possibleActiveModals[1];
    this.activateFileLinkModalSubject.next({
      modalActive: true,
      fileLink: link,
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
      modalFileName,
      modalFileId,
    });
  }

  public activateShareWithUsersModal(
    modalFileName: string,
    modalFileId: string,
    recipients: Recipient[],
    modalFileHasPassword: boolean
  ) {
    if (this.activeModal && this.activeModal !== this.possibleActiveModals[3]) {
      this.deactivateAllModals();
    }
    this.activeModal = this.possibleActiveModals[3];
    this.activateShareWithUsersModalSubject.next({
      modalActive: true,
      modalFileName,
      modalFileId,
      modalFileHasPassword,
      recipients,
    });
  }

  public activateDeleteConfirmModal(
    modalFileName: string,
    modalFileId: string
  ) {
    if (this.activeModal && this.activeModal !== this.possibleActiveModals[4]) {
      this.deactivateAllModals();
    }
    this.activeModal = this.possibleActiveModals[4];
    this.activateDeleteConfirmModalSubject.next({
      modalActive: true,
      modalFileName,
      modalFileId,
    });
  }

  public activateStatisticsModal(
    modalFileName: string,
    modalFileId: string,
    fileLogs: FileLog[]
  ) {
    if (this.activeModal && this.activeModal !== this.possibleActiveModals[5]) {
      this.deactivateAllModals();
    }
    this.activeModal = this.possibleActiveModals[5];
    this.activateStatisticsModalSubject.next({
      modalActive: true,
      modalFileName,
      modalFileId,
      fileLogs,
    });
  }

  public activateChangeExpirationDateModal(
    modalFileName: string,
    modalFileId: string,
    expirationDate: string
  ) {
    if (this.activeModal && this.activeModal !== this.possibleActiveModals[6]) {
      this.deactivateAllModals();
    }
    this.activeModal = this.possibleActiveModals[6];
    this.activateChangeExpirationDateModalSubject.next({
      modalActive: true,
      modalFileName,
      modalFileId,
      expirationDate,
    });
  }

  public deactivateDownloadModal() {
    if (this.activeModal && this.activeModal === this.possibleActiveModals[0]) {
      this.activeModal = ' ';
      this.activateDownloadModalSubject.next({
        modalActive: false,
        modalFileId: '',
        modalFileName: '',
        modalFileHasPassword: false,
      });
    }
  }

  public deactivateFileLinkModal() {
    if (this.activeModal && this.activeModal === this.possibleActiveModals[1]) {
      this.activeModal = ' ';
      this.activateFileLinkModalSubject.next({
        modalActive: false,
        fileLink: '',
      });
    }
  }

  public deactivateAddRecipientsModal() {
    if (this.activeModal && this.activeModal === this.possibleActiveModals[2]) {
      this.activeModal = ' ';
      this.activateAddRecipientsModalSubject.next({
        modalActive: false,
        modalFileName: '',
        modalFileId: '',
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
        recipients: [],
      });
    }
  }

  public deactivateDeleteConfirmModal() {
    if (this.activeModal && this.activeModal === this.possibleActiveModals[4]) {
      this.activeModal = ' ';
      this.activateDeleteConfirmModalSubject.next({
        modalActive: false,
        modalFileName: '',
        modalFileId: '',
      });
    }
  }

  public deactivateStatisticsModal() {
    if (this.activeModal && this.activeModal === this.possibleActiveModals[5]) {
      this.activeModal = ' ';
      this.activateStatisticsModalSubject.next({
        modalActive: false,
        modalFileName: '',
        modalFileId: '',
        fileLogs: [],
      });
    }
  }

  public deactivateChangeExpirationDateModal() {
    if (this.activeModal && this.activeModal === this.possibleActiveModals[6]) {
      this.activeModal = ' ';
      this.activateChangeExpirationDateModalSubject.next({
        modalActive: false,
        modalFileName: '',
        modalFileId: '',
        expirationDate: '',
      });
    }
  }

  private deactivateAllModals() {
    this.deactivateDownloadModal();
    this.deactivateFileLinkModal();
    this.deactivateAddRecipientsModal();
    this.deactivateDeleteConfirmModal();
  }
}
interface DownloadModalValue {
  modalActive: boolean;
  modalFileId: string;
  modalFileName: string;
  modalFileHasPassword: boolean;
}

interface FileLinkModalValue {
  modalActive: boolean;
  fileLink: string;
}

interface AddRecipientsModalValue {
  modalActive: boolean;
  modalFileName: string;
  modalFileId: string;
}

interface ShareWithUsersModalValue {
  modalActive: boolean;
  modalFileName: string;
  modalFileId: string;
  modalFileHasPassword: boolean;
  recipients: Recipient[];
}

interface StatisticsModalValue {
  modalActive: boolean;
  modalFileName: string;
  modalFileId: string;
  fileLogs: FileLog[];
}

interface DeleteConfirmModalValue {
  modalActive: boolean;
  modalFileName: string;
  modalFileId: string;
}

interface ChangeExpirationDateModalValue {
  modalActive: boolean;
  modalFileName: string;
  modalFileId: string;
  expirationDate: string;
}
