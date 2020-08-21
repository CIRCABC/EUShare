/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-cbc-lang-selector',
  templateUrl: './cbc-lang-selector.component.html',
  styleUrls: ['./cbc-lang-selector.component.css']
})
export class CbcLangSelectorComponent implements OnInit {
  @Input()
  public compactMode = false;

  constructor() {}

  ngOnInit(): void {}
}
