/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LastLoginsComponent } from './last-logins/last-logins.component';
import { LastUploadsComponent } from './last-uploads/last-uploads.component';
import { LastDownloadsComponent } from './last-downloads/last-downloads.component';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'app-logs',
  standalone: true,
  imports: [
    CommonModule,
    LastLoginsComponent,
    LastUploadsComponent,
    LastDownloadsComponent,
    MatTabsModule,
    MatIconModule,
    FontAwesomeModule,
  ],
  templateUrl: './logs.component.html',
  styleUrls: ['./logs.component.scss'],
})
export class LogsComponent {}
