/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModalsService {

  private possibleActiveModals: string[] = ['password', 'fileLink', ' '];
  private activeModal!: string;

  private activatePasswordModalSubject = new Subject<PasswordModalValue>();
  public activatePasswordModal$: Observable<PasswordModalValue> = this.activatePasswordModalSubject.asObservable();


  private activateFileLinkModalSubject = new Subject<FileLinkModalValue>();
  public activateFileLinkModal$: Observable<FileLinkModalValue> = this.activateFileLinkModalSubject.asObservable();


  constructor() { }

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

  public activatePasswordModal(modalFileId: string, modalFileName: string) {
    if (this.activeModal && this.activeModal !== this.possibleActiveModals[0]) {
      this.deactivateAllModals();
    }
    this.activeModal = this.possibleActiveModals[0];
    this.activatePasswordModalSubject.next({
      modalActive: true,
      modalFileId: modalFileId,
      modalFileName: modalFileName
    });
  }

  public deactivatePasswordModal() {
    if (this.activeModal && this.activeModal === this.possibleActiveModals[0]) {
      this.activeModal = ' ';
      this.activatePasswordModalSubject.next({
        modalActive: false,
        modalFileId: '',
        modalFileName: ''
      });
    }
  }

  public deactivateFileLinkModal() {
    if (this.activeModal && this.activeModal === this.possibleActiveModals[1]) {
      this.activeModal = ' ';
      this.activateFileLinkModalSubject.next({
        modalActive: false,
        fileLink: ''
      })
    }
  }

  private deactivateAllModals() {
    this.deactivatePasswordModal();
    this.deactivateFileLinkModal();
  }

}
export interface PasswordModalValue {
  modalActive: boolean,
  modalFileId: string,
  modalFileName: string
}

export interface FileLinkModalValue {
  modalActive: boolean,
  fileLink: string
}
