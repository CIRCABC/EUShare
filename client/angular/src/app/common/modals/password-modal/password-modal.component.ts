/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { ModalsService } from '../modals.service';

@Component({
  selector: 'app-password-modal',
  templateUrl: './password-modal.component.html'
})
export class PasswordModalComponent implements OnInit {
  public modalActive!: boolean;
  public modalFileId!: string;
  public modalFileName!: string;

  constructor(private modalService: ModalsService) { }

  ngOnInit() {
    this.modalActive = false;
    this.modalService.activatePasswordModal$.subscribe(nextModalActiveValue => {
      this.modalActive = nextModalActiveValue.modalActive;
      this.modalFileId = nextModalActiveValue.modalFileId;
      this.modalFileName = nextModalActiveValue.modalFileName;
    });
  }

  public closeModal() {
    this.modalService.deactivatePasswordModal();
  }

}
