/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnInit } from '@angular/core';
import {
  UsersService,
  UserInfo,
  MountPointSpace,
  AdminService,
  StatsService,
  Stat,
} from '../openapi';
import { NotificationService } from '../common/notification/notification.service';
import {
  faUser,
  faUserTie,
  faArrowDownWideShort,
} from '@fortawesome/free-solid-svg-icons';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { FileSizeFormatPipe } from '../common/pipes/file-size-format.pipe';
import { TranslocoModule } from '@ngneat/transloco';
import { BarChartComponent } from './bar-chart/bar-chart.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgIf, NgFor } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatTabChangeEvent, MatTabsModule } from '@angular/material/tabs';
import { FileRowContainerComponent } from '../common/uploaded-file-row-container/uploaded-file-row-container.component';
import { TrustComponent } from './trust/trust.component';
import { MatIconModule } from '@angular/material/icon';
import { AbuseComponent } from './abuse/abuse.component';
import { LogsComponent } from './logs/logs/logs.component';
@Component({
  selector: 'app-administration',
  templateUrl: './administration.component.html',
  styleUrls: ['./administration.component.scss'],
  standalone: true,
  imports: [
    ReactiveFormsModule,
    FormsModule,
    NgIf,
    FontAwesomeModule,
    NgFor,
    BarChartComponent,
    TranslocoModule,
    FileSizeFormatPipe,
    MatTabsModule,
    FileRowContainerComponent,
    TrustComponent,
    AbuseComponent,
    LogsComponent,
    MatIconModule,
  ],
})
export class AdministrationComponent implements OnInit {
  public faUser = faUser;
  public faUserTie = faUserTie;
  public faArrowDownWideShort = faArrowDownWideShort;

  public searchIsLoading = false;
  public isAfterSearch = false;
  public isAfterSelected = false;
  public isChangePermissions = false;

  public searchString = '';
  public searchActive = true;

  public sortBy = 'name';

  private pageSize = 10;
  public pageNumber = 0;

  public hasNextPage = false;

  public userInfoArray: UserInfo[] = [];

  public mountPointSpaces: MountPointSpace[] = [];
  public stats: Stat[] = [];
  public yearStats = {} as Stat;

  private selectedUserInfoIndex = 0;

  public valuesInGigaBytes = [
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 20, 25, 30, 35, 40,
    45, 50, 60, 70, 80, 100, 1024,
  ];
  public selectedValueInGigaBytesIndex = 0;

  public selectedValueInGigaBytes = 0;

  public selectedUserRole: UserInfo.RoleEnum = UserInfo.RoleEnum.INTERNAL;

  public roles = Object.values(UserInfo.RoleEnum);

  public changeIsLoading = false;

  public yearList: number[] = [];
  public year = 2022;

  public math = Math;

  public monthsLabels: string[] = [];

  constructor(
    private router: Router,
    private usersApi: UsersService,
    private adminService: AdminService,
    private statsService: StatsService,
    private notificationService: NotificationService,
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
      this.adminService.getDiskSpace(),
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

  public async resultsNextPage() {
    if (await this.isLastPage()) {
      return;
    }

    this.removeSelection();
    this.pageNumber++;

    this.userInfoArray = await firstValueFrom(
      this.usersApi.getUsersUserInfo(
        this.pageSize,
        this.pageNumber,
        this.searchString,
        this.searchActive,
        this.sortBy,
      ),
    );
    this.hasNextPage = !(await this.isLastPage());
  }

  public async resultsPreviousPage() {
    this.removeSelection();
    this.pageNumber--;

    this.userInfoArray = await firstValueFrom(
      this.usersApi.getUsersUserInfo(
        this.pageSize,
        this.pageNumber,
        this.searchString,
        this.searchActive,
        this.sortBy,
      ),
    );
    this.hasNextPage = !(await this.isLastPage());
  }

  public async search() {
    try {
      this.searchIsLoading = true;
      this.isAfterSearch = false;

      this.pageNumber = 0;

      this.removeSelection();

      this.userInfoArray = await firstValueFrom(
        this.usersApi.getUsersUserInfo(
          this.pageSize,
          this.pageNumber,
          this.searchString,
          this.searchActive,
          this.sortBy,
        ),
      );
      this.hasNextPage = !(await this.isLastPage());
      this.isAfterSearch = true;
    } catch (error) {
      // managed in interceptor
    } finally {
      this.searchIsLoading = false;
    }
  }

  public sortByName() {
    this.sortBy = 'name';
    this.search();
  }
  public sortByFiles() {
    this.sortBy = 'files_count';
    this.search();
  }
  public sortByUsage() {
    this.sortBy = 'used_space';
    this.search();
  }

  public displayUserInfoNumber(i: number) {
    this.hideUploadedFiles();
    this.selectedUserInfoIndex = i;
    this.selectedValueInGigaBytes = Math.floor(
      this.userInfoArray[i].totalSpace / (1024 * 1024 * 1024),
    );
    const role = this.userInfoArray[i].role;
    this.selectedUserRole =
      role === undefined ? UserInfo.RoleEnum.INTERNAL : role;

    this.isAfterSelected = true;
  }

  public get selectedUserInfo(): UserInfo {
    return this.userInfoArray[this.selectedUserInfoIndex];
  }

  public removeSelection() {
    this.isAfterSelected = false;
  }

  private async isLastPage() {
    if (this.userInfoArray.length === 0) {
      return true;
    } else if (this.userInfoArray.length < this.pageSize) {
      return true;
    } else {
      const nextPage = await firstValueFrom(
        this.usersApi.getUsersUserInfo(
          this.pageSize,
          this.pageNumber + 1,
          this.searchString,
          this.searchActive,
          this.sortBy,
        ),
      );
      return nextPage.length === 0;
    }
  }

  public async changePermissions() {
    try {
      this.changeIsLoading = true;
      this.selectedUserInfo.totalSpace =
        this.selectedValueInGigaBytes * 1024 * 1024 * 1024;
      this.selectedUserInfo.isAdmin =
        this.selectedUserRole === UserInfo.RoleEnum.ADMIN;
      this.selectedUserInfo.role = this.selectedUserRole;
      await firstValueFrom(
        this.usersApi.putUserUserInfo(
          this.selectedUserInfo.id,
          this.selectedUserInfo,
        ),
      );
      this.notificationService.addSuccessMessageTranslation(
        'change.applied',
        undefined,
        true,
      );
    } catch (error) {
      // managed in the interceptor
    } finally {
      this.changeIsLoading = false;
      this.isChangePermissions = false;
    }
  }

  public toggleChangePermissions() {
    this.isChangePermissions = !this.isChangePermissions;
  }

  public showUploadedFiles = false;
  public toggleUploadedFiles() {
    this.showUploadedFiles = !this.showUploadedFiles;
  }

  public hideUploadedFiles() {
    this.showUploadedFiles = false;
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
