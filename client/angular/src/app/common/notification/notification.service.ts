/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Injectable } from '@angular/core';
import { HashMap } from '@ngneat/transloco';
import { Observable, Subject } from 'rxjs';
import { I18nService } from '../i18n/i18n.service';
import { NotificationLevel } from './notification-level';
import { NotificationMessage } from './notification-message';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private i18nService: I18nService) {}

  public messageSource: Subject<NotificationMessage> =
    new Subject<NotificationMessage>();
  public messageDestroySource: Subject<NotificationMessage> =
    new Subject<NotificationMessage>();
  public messageAnnounced$: Observable<NotificationMessage> =
    this.messageSource.asObservable();
  public messageDestroyed$: Observable<NotificationMessage> =
    this.messageDestroySource.asObservable();
  private lastMessage = { message: '', time: new Date().getTime() };

  public addErrorMessage(
    message: string,
    autoclose = false,
    displayTime?: number,
  ): void {
    this.addMessage(
      message,
      NotificationLevel.ERROR,
      autoclose,
      false,
      displayTime,
    );
  }

  public addErrorMessageTranslation(
    key: string,
    params?: HashMap,
    autoclose = false,
    displayTime?: number,
  ): void {
    this.addMessageTranslation(
      NotificationLevel.ERROR,
      key,
      params,
      autoclose,
      displayTime,
    );
  }

  public addWarningMessageTranslation(
    key: string,
    params?: HashMap,
    autoclose = false,
    displayTime?: number,
  ): void {
    this.addMessageTranslation(
      NotificationLevel.WARNING,
      key,
      params,
      autoclose,
      displayTime,
    );
  }

  public addForbiddenMessageTranslation(
    key: string,
    params?: HashMap,
    autoclose = false,
    displayTime?: number,
  ): void {
    this.addMessageTranslation(
      NotificationLevel.FORBIDDEN,
      key,
      params,
      autoclose,
      displayTime,
    );
  }

  public addSuccessMessageTranslation(
    key: string,
    params?: HashMap,
    autoclose = true,
    displayTime?: number,
  ): void {
    this.addMessageTranslation(
      NotificationLevel.SUCCESS,
      key,
      params,
      autoclose,
      displayTime,
    );
  }

  public addMessageTranslation(
    level: NotificationLevel,
    key: string,
    params?: HashMap,
    autoclose = false,
    displayTime?: number,
  ): void {
    let message: string;
    if (params === undefined) {
      message = this.i18nService.translate(key);
    } else {
      message = this.i18nService.translate(key, params);
    }
    this.addMessage(message, level, autoclose, false, displayTime);
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

  public addTrustMessage(
    message: string,
    level: NotificationLevel,
    autoclose = false,
    displayTime?: number,
  ): void {
    let finalDisplayTime;
    if (displayTime !== undefined) {
      finalDisplayTime = displayTime;
    }

    if (!this.isSameAsLastMessage(message)) {
      const uiMessage = new NotificationMessage(
        level,
        message,
        true,
        autoclose,
        finalDisplayTime,
      );
      this.messageSource.next(uiMessage);
    }
  }

  private addMessage(
    message: string,
    level: NotificationLevel,
    autoclose = false,
    trustDialog = false,
    displayTime?: number,
  ): void {
    let finalDisplayTime;
    if (displayTime !== undefined) {
      finalDisplayTime = displayTime;
    }

    if (!this.isSameAsLastMessage(message)) {
      const uiMessage = new NotificationMessage(
        level,
        message,
        trustDialog,
        autoclose,
        finalDisplayTime,
      );
      this.messageSource.next(uiMessage);
    }
  }
}
