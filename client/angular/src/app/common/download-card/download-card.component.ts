/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import {
  faDownload,
  faChevronLeft,
  faExclamationTriangle
} from '@fortawesome/free-solid-svg-icons';
import { DownloadsService, DownloadInProgress } from '../../services/downloads.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-download-card',
  templateUrl: './download-card.component.html',
  styleUrls: ['./download-card.component.scss']
})
export class DownloadCardComponent implements OnInit {
  public faDownload = faDownload;
  public faChevronLeft = faChevronLeft;
  public faTimes = faExclamationTriangle;

  public enterScreenOrLeaveScreen = 'stayScreen';
  public showDownloadArrow = true;

  public downloadsToDisplay: DownloadToDisplay[] = [];

  private downloadInProgressMap = new Map<string, Observable<DownloadInProgress>>()

  constructor(private downloadsService: DownloadsService) { }

  getDownloadProgress(i: number) {
    return this.downloadsToDisplay[i].percentage;
  }

  getIsSucessOrIsError(i: number) {
    return this.downloadsToDisplay[i].hasError ? 'is-danger' : 'is-success';
  }

  getHasError(i: number) {
    return this.downloadsToDisplay[i].hasError;
  }

  isDisplayed(): boolean {
    return this.enterScreenOrLeaveScreen === 'enterScreen';
  }

  isNotDisplayed(): boolean {
    return (
      this.enterScreenOrLeaveScreen === 'stayScreen' ||
      this.enterScreenOrLeaveScreen === 'leaveScreen'
    );
  }

  display() {
    if (this.isNotDisplayed()) {
      this.hideArrow();
      this.enterScreenOrLeaveScreen = 'enterScreen';
    }
  }

  hide() {
    if (this.isDisplayed()) {
      this.displayArrow();
      this.enterScreenOrLeaveScreen = 'leaveScreen';
    }
  }

  displayArrow() {
    this.showDownloadArrow = true;
  }

  hideArrow() {
    this.showDownloadArrow = false;
  }

  ngOnInit() {
    this.downloadsService.displayArrow$.subscribe(next => {
      this.showDownloadArrow = next;
    });

    this.downloadsService.displayDownloads$.subscribe(next => {
      if (next) {
        this.display();
      } else {
        this.hide();
      }
    });
    this.downloadsService.nextDownloadsInProgress$.subscribe(
      downloadInProgress$ => {
        this.downloadInProgressMap.set(downloadInProgress$.fileId, downloadInProgress$.downloadInProgressObservable);
        downloadInProgress$.downloadInProgressObservable.subscribe(
          downloadInProgress => {
            const index = this.downloadsToDisplay.findIndex(
              element => element.fileId === downloadInProgress.fileId
            );
            const nextDownloadToDisplay = {
              fileName: downloadInProgress.name,
              hasError: false,
              fileId: downloadInProgress.fileId,
              percentage: downloadInProgress.percentage
            };
            if (index !== -1) { // Already existing
              if (
                downloadInProgress.percentage <
                this.downloadsToDisplay[index].percentage &&
                !this.downloadsToDisplay[index].hasError
              ) {
                // Already existing but re-download
                this.downloadsToDisplay.splice(index, 1);
                this.downloadsToDisplay.unshift(nextDownloadToDisplay);
              } else {
                // Already existing and download
                this.downloadsToDisplay[index] = nextDownloadToDisplay;
              }
            } else {
              // Brand new download
              this.downloadsToDisplay.unshift(nextDownloadToDisplay);
            }
          },
          error => {
            const id: string = error.message;
            const index = this.downloadsToDisplay.findIndex(
              element => element.fileId === id
            );
            if (index >= 0) {
              this.downloadsToDisplay[index].hasError = true;
            }
          }
        );
      }
    );
  }
}

export interface DownloadToDisplay {
  fileName: string;
  hasError: boolean;
  fileId: string;
  percentage: number;
}
