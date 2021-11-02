/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { SessionServiceImpl } from '../../openapi/api/session.service.impl';


@Component({
  selector: 'app-shared-with-me',
  templateUrl: './shared-with-me.component.html',
})
export class SharedWithMeComponent implements OnInit {
  public userId!: string;

  constructor(private session: SessionServiceImpl) {}

  ngOnInit() {
    const userIdOrNull = this.session.getStoredId();
    if (userIdOrNull) {
      this.userId = userIdOrNull;
    }
  }
}
