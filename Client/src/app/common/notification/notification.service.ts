import { Injectable } from '@angular/core';
import { NotificationLevel } from './notification-level';
import { NotificationMessage } from './notification-message';
import { Subject, Observable } from 'rxjs';

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

}
