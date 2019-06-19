/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Injectable } from '@angular/core';
import { NotificationLevel } from './notification-level';
import { NotificationMessage } from './notification-message';
import { Subject, Observable } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  public messageSource: Subject<NotificationMessage> = new Subject<NotificationMessage>();
  public messageDestroySource: Subject<NotificationMessage> = new Subject<NotificationMessage>();
  public messageAnnounced$: Observable<NotificationMessage> = this.messageSource.asObservable();
  public messageDestroyed$: Observable<NotificationMessage> = this.messageDestroySource.asObservable();
  private lastMessage = { message: '', time: new Date().getTime() };

  public addInfoMessage(
    message: string,
    autoclose: boolean = false,
    displayTime?: number
  ): void {
    this.addMessage(message, NotificationLevel.INFO, autoclose, displayTime);
  }

  public addErrorMessage(
    message: string,
    autoclose: boolean = false,
    displayTime?: number
  ): void {
    this.addMessage(message, NotificationLevel.ERROR, autoclose, displayTime);
  }

  public addWarningMessage(
    message: string,
    autoclose: boolean = false,
    displayTime?: number
  ): void {
    this.addMessage(message, NotificationLevel.WARNING, autoclose, displayTime);
  }

  public addSuccessMessage(
    message: string,
    autoclose: boolean = false,
    displayTime?: number
  ): void {
    this.addMessage(message, NotificationLevel.SUCCESS, autoclose, displayTime);
  }

  public removeMessage(message: NotificationMessage): void {
    this.messageDestroySource.next(message);
  }

  private isSameAsLastMessage(message: string): boolean {
    let result = false;
    const time = new Date().getTime();
    if (
      message === this.lastMessage.message &&
      time - this.lastMessage.time < 5000
    ) {
      result = true;
    }
    this.lastMessage.message = message;
    this.lastMessage.time = time;
    return result;
  }

  private addMessage(
    message: string,
    level: NotificationLevel,
    autoclose: boolean = false,
    displayTime?: number
  ): void {
    let finalDisplayTime;
    if (displayTime !== undefined) {
      finalDisplayTime = displayTime;
    }

    if (!this.isSameAsLastMessage(message)) {
      const uiMessage = new NotificationMessage(
        level,
        message,
        autoclose,
        finalDisplayTime
      );
      this.messageSource.next(uiMessage);
    }
  }

  public errorMessageToDisplay(httpErrorResponse: HttpErrorResponse, action: string) {
    console.log(httpErrorResponse);
    switch (httpErrorResponse.status) {
      case 400: {
        this.addErrorMessage('The server was unable to understand your request while ' + action + '. Please try again later or contact the support.', false);
        break;
      }
      case 401: {
        this.addErrorMessage('Wrong credentials, please try again.', true);
        break;
      }
      case 403: {
        this.addErrorMessage('You are not authorized to ' + action + '.', false);
        break;
      }
      case 404: {
        this.addErrorMessage('The server was unable to find the resource while' + action + '. Please try again later or contact the support', false);
        break;
      }
      case 500: {
        this.addErrorMessage('An internal server error occured while' + action + '. Please try again later or contact the support', false);
        break;
      }
      default: {
        this.addErrorMessage('An unexepected error occured while ' + action + '! Please contact the support.')
      }
    }
  }

}
