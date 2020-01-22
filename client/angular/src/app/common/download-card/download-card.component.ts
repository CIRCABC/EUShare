/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit } from '@angular/core';
import { faDownload, faChevronLeft, faExclamationTriangle } from '@fortawesome/free-solid-svg-icons';
import { DownloadsService } from '../../services/downloads.service';

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

  public downloadsInProgress: DownloadToDisplay[] = [];

  constructor(private downloadsService: DownloadsService) { }

  getDownloadProgress(i: number) {
    return this.downloadsInProgress[i].percentage;
  }

  getIsSucessOrIsError(i: number) {
    return this.downloadsInProgress[i].hasError ? 'is-danger' : 'is-success';
  }

  getHasError(i: number) {
    return this.downloadsInProgress[i].hasError;
  }

  isDisplayed(): boolean {
    return this.enterScreenOrLeaveScreen === 'enterScreen';
  }

  isNotDisplayed(): boolean {
    return this.enterScreenOrLeaveScreen === 'stayScreen' || this.enterScreenOrLeaveScreen === 'leaveScreen';
  }

  display() {
    if (this.isNotDisplayed()) {
      this.hideArrow();
      this.enterScreenOrLeaveScreen = 'enterScreen';
    }
  }

  hide() {
    if (this.isDisplayed()) {
      console.log('hide!');
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
    this.downloadsService.displayArrow$.subscribe(
      next => {
        this.showDownloadArrow = next;
      }
    )

    this.downloadsService.displayDownloads$.subscribe(
      next => {
        if (next) {
          this.display();
        } else {
          this.hide();
        }
      }
    )
    this.downloadsService.nextDownloadSubjects$.subscribe(
      next => {
        next.subscribe(
          next => {
            const index = this.downloadsInProgress.findIndex(element => element.fileId === next.fileId);
            const nextDownloadToDisplay = {
              fileName: next.name,
              hasError: false,
              fileId: next.fileId,
              percentage: next.percentage
            }
            if (index != -1) {
              if (next.percentage < this.downloadsInProgress[index].percentage && !this.downloadsInProgress[index].hasError) {
                // Already existing but re-download
                this.downloadsInProgress.splice(index, 1);
                this.downloadsInProgress.unshift(nextDownloadToDisplay);
              } else {
                // Already existing and download
                this.downloadsInProgress[index] = nextDownloadToDisplay;
              }
            } else {
              // Brand new download
              this.downloadsInProgress.unshift(nextDownloadToDisplay);
            }
          },
          error => {
            const id: string = error;
            const index = this.downloadsInProgress.findIndex(element => element.fileId === id);
            this.downloadsInProgress[index].hasError = true;
          }
        );
      }
    )
  }

}

export interface DownloadToDisplay {
  fileName: string;
  hasError: boolean;
  fileId: string;
  percentage: number;
}
