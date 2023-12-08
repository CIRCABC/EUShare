/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit, signal } from '@angular/core';
import { AbuseService } from '../../openapi/api/abuse.service';
import { CommonModule, DatePipe } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AbuseReportDetails } from '../../openapi/model/abuseReportDetails';
import { AbuseAdminDialogComponent } from './abuse-admin-dialog.component';
import { TranslocoModule } from '@ngneat/transloco';
import { AbuseDetailsDialogComponent } from './abuse-details-dialog.component';
import { MatTabsModule } from '@angular/material/tabs';
import { AbuseTabComponent } from './abuse-tab.component';
import { AbuseReport } from '../../openapi';
import { firstValueFrom } from 'rxjs/internal/firstValueFrom';

@Component({
  selector: 'app-abuse',
  templateUrl: './abuse.component.html',
  styleUrls: ['./abuse.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    TranslocoModule,
    MatTabsModule,
    AbuseTabComponent,
  ],
  providers: [DatePipe],
})
export class AbuseComponent implements OnInit {
  abuseReportsDetailsMap: { [key: string]: AbuseReportDetails[] } = {};
  public selectedTabIndex = signal(0);

  constructor(private abuseService: AbuseService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.fetchData();
  }

  fetchData(): void {
    this.abuseService
      .getAbuseReportList()
      .subscribe((data: { [key: string]: AbuseReportDetails[] }) => {
        this.abuseReportsDetailsMap = data;
      });
  }

  approveReport(report: AbuseReportDetails | undefined): void {
    if (report) {
      const dialogRef = this.dialog.open(AbuseAdminDialogComponent, {
        data: report,
      });

      dialogRef
        .afterClosed()
        .subscribe((updatedReport: AbuseReportDetails | undefined) => {
          if (updatedReport) {
            this.updateAbuseReportStatus(updatedReport);
          }
        });
    }
  }

  denyReport(updatedReport: AbuseReportDetails | undefined): void {
    if (updatedReport) updatedReport.status = AbuseReport.StatusEnum.Denied;

    if (updatedReport) {
      this.updateAbuseReportStatus(updatedReport);
    }
  }

  private async updateAbuseReportStatus(details: AbuseReportDetails) {
    await firstValueFrom(
      this.abuseService.updateAbuseReport(details.ID as string, details)
    );
    this.fetchData();
  }

  convertToAbuseReport(details: AbuseReportDetails): AbuseReport {
    return {
      ID: details.ID,
      reporter: details.reporter,
      fileId: details.fileId,
      reason: details.reason,
      description: details.description,
      date: details.date,
      status: details.status,
    };
  }

  showList(reports: AbuseReportDetails[]): void {
    this.dialog.open(AbuseDetailsDialogComponent, {
      data: reports,
      width: '80%',
    });
  }

  delete(details: AbuseReportDetails): void {
    if (!details) {
      return;
    }
    if (details.ID)
      this.abuseService
        .deleteAbuseReport(details.ID)
        .subscribe(() => this.ngOnInit());
  }
}
