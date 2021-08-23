/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Injectable } from '@angular/core';
import { HashMap, TranslocoService } from '@ngneat/transloco';
import { Observable, Subject } from 'rxjs';
import { NotificationLevel } from './notification-level';
import { NotificationMessage } from './notification-message';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private translateService: TranslocoService) {}

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
    autoclose: boolean = false,
    displayTime?: number
  ): void {
    this.addMessage(message, NotificationLevel.ERROR, autoclose, displayTime);
  }

  public contactSupport(): string {
    return this.translateService.translate<string>('contact.support');
  }

  public whileTrying(): string {
    return this.translateService.translate<string>('while.trying');
  }

  public to(): string {
    return this.translateService.translate<string>('to');
  }
  public translate(key: string, params?: HashMap): string {
    if (params === undefined) {
      return this.translateService.translate<string>(key);
    } else {
      return this.translateService.translate<string>(key, params);
    }
  }

  public addErrorMessageTranslation(
    key: string,
    params?: HashMap,
    autoclose: boolean = false,
    displayTime?: number
  ): void {
    this.addMessageTranslation(
      NotificationLevel.ERROR,
      key,
      params,
      autoclose,
      displayTime
    );
  }

  public addSuccessMessageTranslation(
    key: string,
    params?: HashMap,
    autoclose: boolean = false,
    displayTime?: number
  ): void {
    this.addMessageTranslation(
      NotificationLevel.SUCCESS,
      key,
      params,
      autoclose,
      displayTime
    );
  }

  public addMessageTranslation(
    level: NotificationLevel,
    key: string,
    params?: HashMap,
    autoclose: boolean = false,
    displayTime?: number
  ): void {
    let message: string;
    if (params === undefined) {
      message = this.translateService.translate<string>(key);
    } else {
      message = this.translateService.translate<string>(key, params);
    }
    this.addMessage(message, level, autoclose, displayTime);
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
}
