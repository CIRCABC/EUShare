/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { AbuseReportDetails } from '../../openapi/model/abuseReportDetails';
import { MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-abuse-details-dialog',
  templateUrl: './abuse-details-dialog.component.html',
  styleUrls: ['./abuse-details-dialog.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatTableModule,
    MatPaginatorModule,
    MatTooltipModule,
  ],
})
export class AbuseDetailsDialogComponent implements OnInit {
  displayedColumns: string[] = [
    'reporter',
    'filename',
    'reason',
    'uploader_email',
    'date',
    'description',
  ];
  dataSource: MatTableDataSource<AbuseReportDetails> =
    new MatTableDataSource<AbuseReportDetails>([]);

  constructor(
    public dialogRef: MatDialogRef<AbuseDetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AbuseReportDetails[],
  ) {}

  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;

  ngOnInit() {
    this.dataSource = new MatTableDataSource<AbuseReportDetails>(this.data);
    this.dataSource.paginator = this.paginator;
    this.dataSource.paginator.pageIndex = 0;
  }

}
