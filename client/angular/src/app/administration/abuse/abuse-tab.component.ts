/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbuseReportDetails } from '../../openapi/model/abuseReportDetails';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-abuse-tab',
  templateUrl: './abuse-tab.component.html',
  styleUrls: ['./abuse-tab.component.scss'],
  standalone: true,
  imports: [CommonModule],
})
export class AbuseTabComponent implements OnInit {
  @Input() reportStatus: string | undefined;
  @Input() abuseReportsDetailsMap:
    | { [key: string]: AbuseReportDetails[] }
    | undefined;

  @Output() showListEvent: EventEmitter<AbuseReportDetails[]> =
    new EventEmitter<AbuseReportDetails[]>();
  @Output() deleteEvent: EventEmitter<string> = new EventEmitter<string>();
  @Output() approveEvent: EventEmitter<AbuseReportDetails> =
    new EventEmitter<AbuseReportDetails>();
  @Output() denyEvent: EventEmitter<AbuseReportDetails> =
    new EventEmitter<AbuseReportDetails>();

  private frontend_url = '';

  ngOnInit(): void {
    this.frontend_url = environment.frontend_url;
  }

  showList(reports: AbuseReportDetails[]): void {
    this.showListEvent.emit(reports);
  }

  delete(id: string): void {
    this.deleteEvent.emit(id);
  }

  approveReport(report: AbuseReportDetails): void {
    this.approveEvent.emit(report);
  }

  denyReport(report: AbuseReportDetails): void {
    this.denyEvent.emit(report);
  }

  formatLink(details: AbuseReportDetails) {
    return `${window.location.protocol}//${window.location.host}${this.frontend_url}/fs/${details.shortUrl}`;
  }
}
