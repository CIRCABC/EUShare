/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule, SortDirection } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { merge, Observable, of as observableOf } from 'rxjs';
import { catchError, map, startWith, switchMap } from 'rxjs/operators';
import { LogService } from '../../../openapi/api/log.service';
import { LastLogin } from '../../../openapi/model/lastLogin';
import { CommonModule, DatePipe } from '@angular/common';
import { SortOrder } from '../../../openapi';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-last-logins',
  templateUrl: './last-logins.component.html',
  styleUrls: ['./last-logins.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    DatePipe,
    MatSortModule,
    MatIconModule,
  ],
})
export class LastLoginsComponent implements AfterViewInit {
  displayedColumns: string[] = [
    'email',
    'name',
    'username',
    'total_space',
    'last_logged',
    'creation_date',
    'uploads',
    'status',
  ];
  data: LastLogin[] = [];
  dataSource = new MatTableDataSource<LastLogin>();
  resultsLength: number = 0;
  isLoadingResults: boolean = true;
  isRateLimitReached: boolean = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private logService: LogService) {}

  ngAfterViewInit() {
    this.sort.sortChange.subscribe(() => (this.paginator.pageIndex = 0));

    merge(this.sort.sortChange, this.paginator.page)
      .pipe(
        startWith({}),
        switchMap(() => {
          this.isLoadingResults = true;
          return this.logService.logGetLastLoginsMetadataGet().pipe(
            switchMap((metadata) => {
              if (metadata.total) {
                this.resultsLength = metadata.total;
              }
              return this.getData(
                this.paginator.pageIndex,
                this.paginator.pageSize,
                this.sort.active,
                this.sort.direction,
              );
            }),
            catchError(() => observableOf(null)),
          );
        }),
        map((data) => {
          this.isLoadingResults = false;
          if (data === null) {
            this.isRateLimitReached = true;
            return [];
          }
          return data;
        }),
      )
      .subscribe((data) => (this.data = data));
  }

  getData(
    pageIndex: number,
    pageSize: number,
    sortField: string,
    sortOrder: SortDirection,
  ): Observable<LastLogin[]> {
    return this.logService.logGetLastLoginsGet(
      pageSize,
      pageIndex,
      sortField,
      this.convertSortDirectionToSortOrder(sortOrder),
    );
  }

  convertSortDirectionToSortOrder(direction: SortDirection): SortOrder {
    switch (direction) {
      case 'asc':
        return SortOrder.Asc;
      case 'desc':
        return SortOrder.Desc;
      default:
        return SortOrder.Asc;
    }
  }

  downloadFile() {
    this.logService
      .logGetAllLastLoginsGet('body', false)
      .subscribe((data: Blob) => {
        const blob = new Blob([data], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);

        const link = document.createElement('a');
        link.href = url;
        link.download = 'last_logins.csv';

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      });
  }
}
