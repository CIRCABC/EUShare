/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component } from '@angular/core';
import { environment } from '../../environments/environment';
import buildInfo  from "../../build";
@Component({
  selector: 'cbc-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
  preserveWhitespaces: true,
})
export class FooterComponent {
  public appAlfVersion = '';
  public appVersion = '0.1';
  public nodeName = '';
  public buildDate = '';
  public circabc_url: string = environment.circabc_url;
  public buildVersion: string;
  public buildCommit: string;
  public buildTimestamp: string;
  constructor(){
    this.buildVersion = buildInfo.version;
    this.buildCommit = "";
    if(buildInfo.git.hash) {
     this.buildCommit = buildInfo.git.hash;
    }
    this.buildTimestamp = buildInfo.timestamp
  }
}
