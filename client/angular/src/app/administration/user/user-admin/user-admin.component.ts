/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  faUser,
  faUserTie,
  faArrowDownWideShort,
} from '@fortawesome/free-solid-svg-icons';
import { NotificationService } from '../../../common/notification/notification.service';
import { UsersService } from '../../../openapi/api/users.service';
import { UserInfo } from '../../../openapi/model/userInfo';
import { firstValueFrom } from 'rxjs/internal/firstValueFrom';
import { TranslocoModule } from '@ngneat/transloco';
import { FileRowContainerComponent } from '../../../common/uploaded-file-row-container/uploaded-file-row-container.component';
import { FileSizeFormatPipe } from '../../../common/pipes/file-size-format.pipe';
import { MatIconModule } from '@angular/material/icon';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-user-admin',
  standalone: true,
  templateUrl: './user-admin.component.html',
  styleUrls: ['./user-admin.component.scss'],
  imports: [
    CommonModule,
    FormsModule,
    FontAwesomeModule,
    TranslocoModule,
    FileRowContainerComponent,
    FileSizeFormatPipe,
    MatIconModule,
  ],
})
export class UserAdminComponent {
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

  public changeIsLoading = false;

  public userInfoArray: UserInfo[] = [];

  private selectedUserInfoIndex = 0;

  public valuesInGigaBytes = [
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 20, 25, 30, 35, 40,
    45, 50, 60, 70, 80, 100, 1024,
  ];
  public selectedValueInGigaBytesIndex = 0;

  public selectedValueInGigaBytes = 0;

  public selectedUserRole: UserInfo.RoleEnum = UserInfo.RoleEnum.Internal;
  public selectedUserStatus: UserInfo.StatusEnum = UserInfo.StatusEnum.Regular;

  public roles = Object.values(UserInfo.RoleEnum);
  public status = Object.values(UserInfo.StatusEnum);

  constructor(
    private usersApi: UsersService,
    private notificationService: NotificationService,
  ) {}

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
    const statu = this.userInfoArray[i].status;
    this.selectedUserRole = role ?? UserInfo.RoleEnum.Internal;
    this.selectedUserStatus = statu ?? UserInfo.StatusEnum.Regular;
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
        this.selectedUserRole === UserInfo.RoleEnum.Admin;
      this.selectedUserInfo.role = this.selectedUserRole;
      this.selectedUserInfo.status = this.selectedUserStatus;
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

  public async freezeAllFiles() {
    await firstValueFrom(
      this.usersApi.putUserUserInfo(
        this.selectedUserInfo.id,
        this.selectedUserInfo,
        true,
      ),
    );

    this.toggleUploadedFiles();
    this.notificationService.addSuccessMessageTranslation(
      'change.applied',
      undefined,
      true,
    );
  }

  public async unfreezeAllFiles() {
    await firstValueFrom(
      this.usersApi.putUserUserInfo(
        this.selectedUserInfo.id,
        this.selectedUserInfo,
        false,
      ),
    );
    this.toggleUploadedFiles();

    this.notificationService.addSuccessMessageTranslation(
      'change.applied',
      undefined,
      true,
    );
  }
}
