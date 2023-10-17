/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { MonitoringService } from '../../openapi/api/monitoring.service';
import { CommonModule, DatePipe } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MonitoringAdminDialogComponent } from './monitoring-admin-dialog.component';
import { TranslocoModule } from '@ngneat/transloco';
import { MatTabsModule } from '@angular/material/tabs';
import { MonitoringTabComponent } from './monitoring-tab.component';
import { Monitoring, MonitoringDetails } from '../../openapi';
import { firstValueFrom } from 'rxjs/internal/firstValueFrom';

@Component({
  selector: 'app-monitoring',
  templateUrl: './monitoring.component.html',
  styleUrls: ['./monitoring.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    TranslocoModule,
    MatTabsModule,
    MonitoringTabComponent,
  ],
  providers: [DatePipe],
})
export class MonitoringComponent implements OnInit {
  monitoringDetails: MonitoringDetails[] = [];

  constructor(
    private monitoringService: MonitoringService,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.fetchData();
  }
  fetchData(): void {
    this.monitoringService
      .getMonitoringEntryList()
      .subscribe((data: MonitoringDetails[]) => {
        this.monitoringDetails = data;
      });
  }

  approveReport(report: MonitoringDetails | undefined): void {
    if (report) {
      const dialogRef = this.dialog.open(MonitoringAdminDialogComponent, {
        data: report,
      });

      dialogRef
        .afterClosed()
        .subscribe((updatedReport: MonitoringDetails | undefined) => {
          if (updatedReport) {
            this.updateMonitoringStatus(updatedReport);
          }
        });
    }
  }

  denyReport(updatedReport: MonitoringDetails | undefined): void {
    if (updatedReport) updatedReport.status = Monitoring.StatusEnum.Denied;

    if (updatedReport) {
      this.updateMonitoringStatus(updatedReport);
    }
  }

  private async updateMonitoringStatus(details: MonitoringDetails) {
    if(details.ID){
      await firstValueFrom(this.monitoringService.updateMonitoringEntry(details.ID,details));
      this.fetchData();
    }
  }

  convertToMonitoring(details: MonitoringDetails): Monitoring {
    return {
      ID: details.ID,
      datetime: details.datetime,
      event: details.event,
      fileId: details.fileId,
      userId: details.userId,
      counter: details.counter,
      status: details.status,
    };
  }


  delete(details: MonitoringDetails): void {
    if (!details) {
      return;
    }
    if (details.ID)
      this.monitoringService
        .deleteMonitoringEntry(details.ID)
        .subscribe(() => this.ngOnInit());
  }
}
