/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit, ViewChild } from '@angular/core';

import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { TrustLog, TrustLogService } from '../../openapi';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-trust-log-table',
  templateUrl: './trust-log-table.component.html',
  styleUrls: ['./trust-log-table.component.scss'],
  standalone: true,
  imports: [MatTableModule, MatSortModule, MatPaginatorModule, CommonModule],
})
export class TrustLogTableComponent implements OnInit {
  displayedColumns: string[] = ['trustDate', 'origin', 'trusted', 'truster'];
  dataSource = new MatTableDataSource<TrustLog>([]);

  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;

  constructor(private trustLogService: TrustLogService) {}

  ngOnInit() {
    this.trustLogService.getAllTrustLogs().subscribe(
      (data) => {
        this.dataSource.data = data;
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
      },
      (error) => {
        console.error('Error fetching Trust Logs:', error);
      },
    );
  }
}
