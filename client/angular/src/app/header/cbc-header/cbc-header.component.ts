/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component } from '@angular/core';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-cbc-header',
  templateUrl: './cbc-header.component.html',
  styleUrls: ['./cbc-header.component.scss'],
})
export class CbcHeaderComponent {
  public circabc_url: string = environment.circabc_url;
  isGuest() {
    return true;
  }
}
