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

import { CommonModule } from '@angular/common';
import { LastDownload } from '../../../openapi/model/lastDownload';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-last-downloads',
  templateUrl: './last-downloads.component.html',
  styleUrls: ['./last-downloads.component.scss'],
  standalone: true,
  imports: [CommonModule, MatTableModule, MatPaginatorModule, MatIconModule],
})
export class LastDownloadsComponent implements AfterViewInit {
  displayedColumns: string[] = [
    'uploader_email',
    'recipient',
    'filename',
    'path',
    'password',
    'shorturl',
    'download_notification',
    'download_date',
  ];
  dataSource = new MatTableDataSource<LastDownload>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private logService: LogService) {}

  fetchData() {
    const pageSize = this.paginator.pageSize || 10;
    const pageNumber = this.paginator.pageIndex || 0;

    this.logService
      .logGetLastDownloadsGet(pageSize, pageNumber)
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
