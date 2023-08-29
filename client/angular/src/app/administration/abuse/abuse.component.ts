/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { AbuseService } from '../../openapi/api/abuse.service';
import { AbuseReport } from '../../openapi/model/abuseReport';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AbuseReportDetails } from '../../openapi/model/abuseReportDetails';
import { AbuseAdminDialogComponent } from './abuse-admin-dialog.component';

@Component({
  selector: 'app-abuse',
  templateUrl: './abuse.component.html',
  styleUrls: ['./abuse.component.scss'],
  standalone: true,
  imports: [CommonModule, MatDialogModule],
})
export class AbuseComponent implements OnInit {
  abuseReportsDetails: AbuseReportDetails[] = [];

  constructor(
    private abuseService: AbuseService,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.abuseService
      .getAbuseReportList()
      .subscribe((data: AbuseReportDetails[]) => {
        this.abuseReportsDetails = data;
      });
  }

  onOpen(report: AbuseReportDetails | undefined): void {
    const id = report?.ID;
    if (!id) {
      return;
    }

    const dialogRef = this.dialog.open(AbuseAdminDialogComponent, {
      data: report,
    });

    /*  dialogRef.afterClosed().subscribe((result) => {
        if (result.action) {
          this.abuseService.updateAbuseReport(
            (id, result)
            .subscribe(() => this.ngOnInit());
        } else {
          this.AbuseService
            .approveAbuseRequest(id, false, result.denyReason)
            .subscribe(() => this.ngOnInit());
        }
      });
    }*/
    /*
      onDelete(id: string | undefined): void {
        if (!id) {
          return;
        }
        this.AbuseService.deleteAbuseRequest(id).subscribe(() => this.ngOnInit());
      }*/
  }
}
