/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-cbc-personal-menu',
  templateUrl: './cbc-personal-menu.component.html',
  styleUrls: ['./cbc-personal-menu.component.scss']
})
export class CbcPersonalMenuComponent implements OnInit {
  public useEULogin = true;
  public user = {
    firstname: 'hello',
    lastname: 'world',
    avatar: 'doesntmatter'
  };
  constructor() {}

  ngOnInit(): void {}

  euLogin() {
    return true;
  }
  isGuest() {
    return true;
  }

  isUser() {
    return true;
  }

  isAppAdmin() {
    return true;
  }
}
