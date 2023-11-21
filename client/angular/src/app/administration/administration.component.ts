/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import { MountPointSpace, AdminService, StatsService, Stat } from '../openapi';

import { firstValueFrom } from 'rxjs';
import { FileSizeFormatPipe } from '../common/pipes/file-size-format.pipe';
import { TranslocoModule } from '@ngneat/transloco';
import { BarChartComponent } from './bar-chart/bar-chart.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatTabChangeEvent, MatTabsModule } from '@angular/material/tabs';
import { FileRowContainerComponent } from '../common/uploaded-file-row-container/uploaded-file-row-container.component';
import { TrustComponent } from './trust/trust.component';
import { MatIconModule } from '@angular/material/icon';
import { AbuseComponent } from './abuse/abuse.component';
import { LogsComponent } from './logs/logs.component';
import { UserAdminComponent } from './user/user-admin/user-admin.component';
import { MonitoringComponent } from './monitoring/monitoring.component';
@Component({
  selector: 'app-administration',
  templateUrl: './administration.component.html',
  styleUrls: ['./administration.component.scss'],
  standalone: true,
  imports: [
    ReactiveFormsModule,
    FormsModule,
    FontAwesomeModule,
    BarChartComponent,
    TranslocoModule,
    FileSizeFormatPipe,
    MatTabsModule,
    FileRowContainerComponent,
    TrustComponent,
    AbuseComponent,
    MonitoringComponent,
    LogsComponent,
    MatIconModule,
    UserAdminComponent,
  ],
})
export class AdministrationComponent implements OnInit {
  public mountPointSpaces: MountPointSpace[] = [];
  public stats: Stat[] = [];
  public yearStats = {} as Stat;

  public yearList: number[] = [];
  public year = 2022;

  public math = Math;

  public monthsLabels: string[] = [];

  constructor(
    private adminService: AdminService,
    private statsService: StatsService
  ) {}

  ngOnInit(): void {
    this.year = new Date().getFullYear();
    for (let y = 2022; y <= this.year; y++) {
      this.yearList.push(y);
    }
    this.getMountPointSpaces();
    this.getStats(this.year);

    for (let i = 1; i <= 12; i++) {
      this.monthsLabels[i - 1] = this.getShortMonthName(i);
    }
  }

  selectedTabIndex = 0;

  selectedTabChange(event: MatTabChangeEvent) {
    this.selectedTabIndex = event.index;
  }

  public async getMountPointSpaces() {
    this.mountPointSpaces = await firstValueFrom(
      this.adminService.getDiskSpace()
    );
  }

  public async getStats(year: number) {
    this.stats = await firstValueFrom(this.statsService.getStats(year));
    this.stats.sort((a: Stat, b: Stat) => {
      return a.month > b.month ? 1 : -1;
    });

    this.yearStats.users = 0;
    this.yearStats.downloads = 0;
    this.yearStats.uploads = 0;
    this.yearStats.downloadsData = 0;
    this.yearStats.uploadsData = 0;

    this.stats.forEach((month) => {
      this.yearStats.users += month.users;
    });
    this.stats.forEach((month) => {
      this.yearStats.downloads += month.downloads;
    });
    this.stats.forEach((month) => {
      this.yearStats.uploads += month.uploads;
    });
    this.stats.forEach((month) => {
      this.yearStats.downloadsData += month.downloadsData;
    });
    this.stats.forEach((month) => {
      this.yearStats.uploadsData += month.uploadsData;
    });

    this.updateGraph('users', 'Users');
  }

  public updateGraph(columnData: string, columnLabel: string) {
    this.data = {
      labels: this.monthsLabels,
      datasets: [
        {
          data: this.stats.map((stats) => Reflect.get(stats, columnData)),
          label: columnLabel,
        },
      ],
    };
  }

  public getMonthName(monthNumber: number) {
    const date = new Date();
    date.setDate(1);
    date.setMonth(monthNumber - 1);
    return date.toLocaleString('default', { month: 'long' });
  }

  public getShortMonthName(monthNumber: number) {
    const date = new Date();
    date.setDate(1);
    date.setMonth(monthNumber - 1);
    return date.toLocaleString('default', { month: 'short' });
  }

  public changeYear() {
    this.getStats(this.year);
  }

  data = {
    labels: ['', '', '', '', '', '', '', '', '', '', '', ''],
    datasets: [{ data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], label: '' }],
  };
}
