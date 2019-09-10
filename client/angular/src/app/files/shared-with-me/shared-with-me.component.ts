/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import {
  SessionService,
} from '../../openapi';

@Component({
  selector: 'app-shared-with-me',
  templateUrl: './shared-with-me.component.html',
  styleUrls: ['./shared-with-me.component.css']
})
export class SharedWithMeComponent implements OnInit {
  public userId!: string;

  constructor(
    private session: SessionService,
  ) { }

  ngOnInit() {
    const userIdOrNull = this.session.getStoredId();
    if (userIdOrNull) {
      this.userId = userIdOrNull;
    }
  }
}
