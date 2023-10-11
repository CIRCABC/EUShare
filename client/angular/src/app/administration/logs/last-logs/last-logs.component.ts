/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { LogService } from '../../../openapi/api/log.service';
import { LastLog } from '../../../openapi/model/lastLog';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-last-logs',
  templateUrl: './last-logs.component.html',
  styleUrls: ['./last-logs.component.scss'],
  standalone: true,
  imports: [CommonModule, MatTableModule, MatPaginatorModule],
})
export class LastLogsComponent implements AfterViewInit {
  displayedColumns: string[] = [
    'id',
    'email',
    'name',
    'username',
    'total_space',
    'last_logged',
    'status',
  ];
  dataSource = new MatTableDataSource<LastLog>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private logService: LogService) {}

  fetchData() {
    const pageSize = this.paginator.pageSize || 10; // Default to 10 if not set
    const pageNumber = this.paginator.pageIndex || 0; // Default to first page if not set

    this.logService
      .logGetLastLogsGet(pageSize, pageNumber)
      .subscribe((data) => {
        this.dataSource.data = data;
      });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.paginator.pageIndex = 0;
    this.paginator.page.subscribe(() => {
      this.fetchData();
    });
    this.fetchData();
  }
}
