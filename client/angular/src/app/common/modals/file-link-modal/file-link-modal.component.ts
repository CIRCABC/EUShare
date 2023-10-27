/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { ModalsService } from '../modals.service';
import { NotificationService } from '../../notification/notification.service';

@Component({
  selector: 'app-file-link-modal',
  templateUrl: './file-link-modal.component.html',
  standalone: true,
})
export class FileLinkModalComponent implements OnInit {
  public modalActive: boolean = false;
  public fileLink!: string;
  public isLoading: boolean = false;

  constructor(
    private modalService: ModalsService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit() {
    this.modalService.activateFileLinkModal$.subscribe(
      (nextModalActiveValue) => {
        this.modalActive = nextModalActiveValue.modalActive;
        this.fileLink = nextModalActiveValue.fileLink;
      },
    );
  }

  copyLink(inputElement: any) {
    this.isLoading = true;
    inputElement.select();
    document.execCommand('copy');
    inputElement.setSelectionRange(0, 0);
    this.notificationService.addSuccessMessageTranslation('file.link.copied');
    setTimeout(() => {
      this.isLoading = false;
    }, 300);
  }

  closeModal() {
    this.modalService.deactivateFileLinkModal();
  }
}
