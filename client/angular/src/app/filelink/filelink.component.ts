/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ModalsService } from '../common/modals/modals.service';
import { I18nService } from '../common/i18n/i18n.service';

@Component({
  selector: 'app-filelink',
  templateUrl: './filelink.component.html',
  styleUrls: ['./filelink.component.scss'],
})
export class FilelinkComponent implements OnInit {
  public fileName!: string;
  public isFilePasswordProtected!: boolean;
  public fileId!: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private modalService: ModalsService,
    private i18nService: I18nService
  ) {
    this.i18nService.configureI18n();
  }

  download() {
    this.modalService.activateDownloadModal(
      this.fileId,
      this.fileName,
      this.isFilePasswordProtected
    );
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    const fileNameB64URIEncoded =
      this.route.snapshot.paramMap.get('filenameb64');
    const isPasswordProtected = this.route.snapshot.paramMap.get(
      'isPasswordProtected'
    );
    if (id && fileNameB64URIEncoded && isPasswordProtected) {
      this.fileId = id;
      this.fileName = atob(decodeURIComponent(fileNameB64URIEncoded));
      this.isFilePasswordProtected = isPasswordProtected === '1';
    } else {
      this.router.navigateByUrl('/home');
    }
  }
}
