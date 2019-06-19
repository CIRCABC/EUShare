/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Component, OnInit, Input } from '@angular/core';
import { NotificationMessage } from './notification-message';
import { NotificationService } from './notification.service';
import { NotificationLevel } from './notification-level';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit {

  @Input()
  public message!: NotificationMessage;
  public animationClass = 'ui-message-show';

  constructor(private notificationService: NotificationService) { }

  ngOnInit(): void {
    if (this.message) {
      if (this.message.autoclose) {
        let timeOutIntrevalInMiliSeconds = 3000;
        if (this.message.displayTime !== undefined) {
          timeOutIntrevalInMiliSeconds =
            this.message.displayTime * 1000;
        }
        setTimeout(() => {
          this.animationClass = 'ui-message-destroy';
        }, (timeOutIntrevalInMiliSeconds - 400));

        setTimeout(() => {
          this.notificationService.removeMessage(this.message);
        }, timeOutIntrevalInMiliSeconds);
      }
    }
  }

  public getClassPerLevel(notificationLevel: NotificationLevel) {
    switch (notificationLevel) {
      case NotificationLevel.SUCCESS: {
        return 'notification is-success';
      }
      case NotificationLevel.INFO: {
        return 'notification is-info';
      }
      case NotificationLevel.WARNING: {
        return 'notification is-warning';
      }
      case NotificationLevel.ERROR: {
        return 'notification is-danger';
      }
    }
  }

  public closeMessage(): void {
    this.notificationService.removeMessage(this.message);
  }

}
