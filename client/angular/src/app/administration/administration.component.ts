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

@Component({
  selector: 'app-administration',
  templateUrl: './administration.component.html',
  styleUrls: ['./administration.component.scss'],
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

  public selectedIsAdminValue = false;
  public changeIsLoading = false;

  public math = Math;

  constructor(
    private router: Router,
    private usersApi: UsersService,
    private adminService: AdminService,
    private statsService: StatsService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.getMountPointSpaces();
    this.getStats(2023);
   
  }

  public async getMountPointSpaces() {
    this.mountPointSpaces = await firstValueFrom(
      this.adminService.getDiskSpace()
    );
  }

  public async getStats(year: number) {
    this.stats = await firstValueFrom(this.statsService.getStats(year));

    this.yearStats.users=0;
    this.yearStats.downloads=0;
    this.yearStats.uploads=0;
    this.yearStats.downloadsData=0;
    this.yearStats.uploadsData=0;
    this.stats.forEach((month) => { console.log(month.users);
      this.yearStats.users += month.users;
    });
    this.stats.forEach((month) => { console.log(month.downloads);
      this.yearStats.downloads += month.downloads;
    });
    this.stats.forEach((month) => { console.log(month.downloads);
      this.yearStats.uploads += month.uploads;
    });
    this.stats.forEach((month) => { console.log(month.downloads);
      this.yearStats.downloadsData += month.downloadsData;
    });
    this.stats.forEach((month) => { console.log(month.downloads);
      this.yearStats.uploadsData += month.uploadsData;
    });
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
        this.sortBy
      )
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
        this.sortBy
      )
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
          this.sortBy
        )
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
    this.selectedUserInfoIndex = i;
    this.selectedValueInGigaBytes = Math.floor(
      this.userInfoArray[i].totalSpace / (1024 * 1024 * 1024)
    );
    this.selectedIsAdminValue = this.userInfoArray[i].isAdmin;
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
          this.sortBy
        )
      );
      return nextPage.length === 0;
    }
  }

  public async changePermissions() {
    try {
      this.changeIsLoading = true;
      this.selectedUserInfo.totalSpace =
        this.selectedValueInGigaBytes * 1024 * 1024 * 1024;
      this.selectedUserInfo.isAdmin = this.selectedIsAdminValue;
      await firstValueFrom(
        this.usersApi.putUserUserInfo(
          this.selectedUserInfo.id,
          this.selectedUserInfo
        )
      );
      this.notificationService.addSuccessMessageTranslation(
        'change.applied',
        undefined,
        true
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

  public goToUserUploadedFiles() {
    this.router.navigate([
      '/administration',
      this.selectedUserInfo.id,
      'files',
      { userName: this.selectedUserInfo.givenName },
    ]);
  }

  public getMonthName(monthNumber: number) {
    const date = new Date();
    date.setMonth(monthNumber - 1);

    return date.toLocaleString('en-US', { month: 'long' });
  }
}
