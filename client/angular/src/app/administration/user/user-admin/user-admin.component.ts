/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  faUser,
  faUserTie,
  faArrowDownWideShort,
} from '@fortawesome/free-solid-svg-icons';
import { NotificationService } from '../../../common/notification/notification.service';

import { UserInfo } from '../../../openapi/model/userInfo';
import { firstValueFrom } from 'rxjs/internal/firstValueFrom';
import { TranslocoModule } from '@ngneat/transloco';
import { FileRowContainerComponent } from '../../../common/uploaded-file-row-container/uploaded-file-row-container.component';
import { FileSizeFormatPipe } from '../../../common/pipes/file-size-format.pipe';
import { MatIconModule } from '@angular/material/icon';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConfirmDialogComponent } from './confirm-dialog.component';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule, SortDirection } from '@angular/material/sort';
import { merge, Observable, of as observableOf } from 'rxjs';
import { catchError, map, startWith, switchMap } from 'rxjs/operators';
import { UsersService } from '../../../openapi';

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
    MatDialogModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatIconModule,
  ],
})
export class UserAdminComponent implements AfterViewInit {
  public faUser = faUser;
  public faUserTie = faUserTie;
  public faArrowDownWideShort = faArrowDownWideShort;

  public searchIsLoading = false;
  public isAfterSearch = false;
  public isAfterSelected = false;
  public isChangePermissions = false;

  public searchString = '';

  private pageSize = 10;
  public pageNumber = 0;

  public hasNextPage = false;

  public changeIsLoading = false;

  data: UserInfo[] = [];
  dataSource = new MatTableDataSource<UserInfo>();
  resultsLength = 0;
  isLoadingResults = true;
  isRateLimitReached = false;

  displayedColumns: string[] = ['name', 'email', 'filesCount', 'usedSpace'];

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

  private previousUserRole: UserInfo.RoleEnum = UserInfo.RoleEnum.External;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private usersService: UsersService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngAfterViewInit() {
    this.search();
  }

  search() {
    this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));

    merge(this.sort.sortChange, this.paginator.page)
      .pipe(
        startWith({}),
        switchMap(() => {
          this.isLoadingResults = true;
          return this.usersService.usersGetUserInfoMetadataGet().pipe(
            switchMap((metadata) => {
              if (metadata.total) {
                this.resultsLength = metadata.total;
              }
              return this.getData(
                this.paginator.pageIndex,
                this.paginator.pageSize,
                this.sort.active,
                this.sort.direction
              );
            }),
            catchError(() => observableOf(null))
          );
        }),
        map((data) => {
          this.isLoadingResults = false;
          if (data === null) {
            this.isRateLimitReached = true;
            return [];
          }
          return data;
        })
      )
      .subscribe((data) => (this.data = data));
  }

  getData(
    pageIndex: number,
    pageSize: number,
    sortField: string,
    sortOrder: SortDirection
  ): Observable<UserInfo[]> {
    return this.usersService.getUsersUserInfo(
      pageSize,
      pageIndex,
      this.searchString,
      sortField,
      this.convertSortDirectionToSortOrder(sortOrder)
    );
  }

  convertSortDirectionToSortOrder(direction: SortDirection): 'ASC' | 'DESC' {
    return direction === 'asc' ? 'ASC' : 'DESC';
  }

  public displayUserInfoNumber(i: number) {
    this.hideUploadedFiles();
    this.selectedUserInfoIndex = i;
    this.selectedValueInGigaBytes = Math.floor(
      this.data[i].totalSpace / (1024 * 1024 * 1024)
    );

    const role = this.data[i].role;
    const statu = this.data[i].status;
    this.selectedUserRole = role ?? UserInfo.RoleEnum.Internal;
    this.previousUserRole = this.selectedUserRole;
    this.selectedUserStatus = statu ?? UserInfo.StatusEnum.Regular;
    this.isAfterSelected = true;
  }

  public get selectedUserInfo(): UserInfo {
    return this.data[this.selectedUserInfoIndex];
  }

  public removeSelection() {
    this.isAfterSelected = false;
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
        this.usersService.putUserUserInfo(
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

  public showUploadedFiles = false;
  public toggleUploadedFiles() {
    this.showUploadedFiles = !this.showUploadedFiles;
  }

  public hideUploadedFiles() {
    this.showUploadedFiles = false;
  }

  public async freezeAllFiles() {
    await firstValueFrom(
      this.usersService.putUserUserInfo(
        this.selectedUserInfo.id,
        this.selectedUserInfo,
        true
      )
    );

    this.toggleUploadedFiles();
    this.notificationService.addSuccessMessageTranslation(
      'change.applied',
      undefined,
      true
    );
  }

  public async unfreezeAllFiles() {
    await firstValueFrom(
      this.usersService.putUserUserInfo(
        this.selectedUserInfo.id,
        this.selectedUserInfo,
        false
      )
    );
    this.toggleUploadedFiles();

    this.notificationService.addSuccessMessageTranslation(
      'change.applied',
      undefined,
      true
    );
  }

  public updateQuotaBasedOnRole() {
    const rolesThatGet1GB = [
      UserInfo.RoleEnum.External,
      UserInfo.RoleEnum.TrustedExternal,
    ];
    this.selectedValueInGigaBytes = rolesThatGet1GB.includes(
      this.selectedUserRole
    )
      ? 1
      : 5;
    this.selectedValueInGigaBytesIndex = this.valuesInGigaBytes.indexOf(
      this.selectedValueInGigaBytes
    );
  }

  public confirmRoleChange(newRole: UserInfo.RoleEnum) {
    if (
      (this.selectedUserRole === UserInfo.RoleEnum.External ||
        this.selectedUserRole === UserInfo.RoleEnum.TrustedExternal) &&
      (newRole === UserInfo.RoleEnum.Internal ||
        newRole === UserInfo.RoleEnum.Admin)
    ) {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '250px',
        data: {
          message: `Are you sure you want to change the role to ${newRole}?`,
        },
      });

      dialogRef.afterClosed().subscribe((confirmed) => {
        if (confirmed) {
          this.selectedUserRole = newRole;
          this.updateQuotaBasedOnRole();
        } else {
          this.selectedUserRole = newRole;
          setTimeout(() => (this.selectedUserRole = this.previousUserRole));
        }
      });
    } else {
      this.selectedUserRole = newRole;
      this.updateQuotaBasedOnRole();
    }
  }
}
