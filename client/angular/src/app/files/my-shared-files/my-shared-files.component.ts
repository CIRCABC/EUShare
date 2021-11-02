/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SessionServiceImpl } from '../../openapi/api/session.service.impl';


@Component({
  selector: 'app-my-shared-files',
  templateUrl: './my-shared-files.component.html',
})
export class MySharedFilesComponent implements OnInit {
  public myId!: string;

  constructor(private session: SessionServiceImpl, private router: Router) {}

  public ngOnInit() {
    const id = this.session.getStoredId();
    if (id) {
      this.myId = id;
    } else {
      this.router.navigateByUrl('');
    }
  }
}
