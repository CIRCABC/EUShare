/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MonitoringDetails } from '../../openapi/model/monitoringDetails';
import { CommonModule, DatePipe } from '@angular/common';
import { environment } from '../../../environments/environment';
import { DownloadsService } from '../../services/downloads.service';

@Component({
  selector: 'app-monitoring-tab',
  templateUrl: './monitoring-tab.component.html',
  styleUrls: ['./monitoring-tab.component.scss'],
  standalone: true,
  imports: [CommonModule],
})
export class MonitoringTabComponent implements OnInit {
  @Input() reportStatus: MonitoringDetails.StatusEnum | undefined;
  @Input() monitoringDetails: MonitoringDetails[] | undefined;

  @Output() deleteEvent: EventEmitter<MonitoringDetails> =
    new EventEmitter<MonitoringDetails>();
  @Output() approveEvent: EventEmitter<MonitoringDetails> =
    new EventEmitter<MonitoringDetails>();
  @Output() denyEvent: EventEmitter<MonitoringDetails> =
    new EventEmitter<MonitoringDetails>();

  private frontend_url = '';

  constructor(
    private downloadsService: DownloadsService,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    this.frontend_url = environment.frontend_url;
  }

  delete(report: MonitoringDetails): void {
    this.deleteEvent.emit(report);
  }

  approveReport(report: MonitoringDetails): void {
    this.approveEvent.emit(report);
  }

  denyReport(report: MonitoringDetails): void {
    this.denyEvent.emit(report);
  }

  formatLink(details: MonitoringDetails) {
    return `${window.location.protocol}//${window.location.host}${this.frontend_url}/fs/${details.shortUrl}`;
  }

  formatString(input: string | undefined): string {
    if (input) {
      const words = input.toLowerCase().split('_');
      words[0] = words[0].charAt(0).toUpperCase() + words[0].slice(1);
      words[words.length - 1] = `(by ${words[words.length - 1]})`;
      return words.join(' ');
    }
    return '';
  }

  public async tryDownload(details: MonitoringDetails | undefined) {
    if (details)
      if (details.fileId && details.filename)
        await this.downloadsService.download(details.fileId, details.filename);
  }
}
