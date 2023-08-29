/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Component, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { NotificationMessage } from './notification-message';
import { NotificationService } from './notification.service';
import { NotificationComponent } from './notification.component';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-notification-system',
  templateUrl: './notification-system.component.html',
  styleUrls: ['./notification-system.component.scss'],
  standalone: true,
  imports: [NgFor, NotificationComponent],
})
export class NotificationSystemComponent implements OnDestroy {
  public messages: NotificationMessage[] = [];
  private messageAnnouncedSubscription$: Subscription;
  private messageDestroyedSubscription$: Subscription;

  public constructor(private notificationService: NotificationService) {
    this.messageAnnouncedSubscription$ =
      this.notificationService.messageAnnounced$.subscribe(
        (message: NotificationMessage) => {
          this.messages.push(message);
          window.scroll(0, 0);
        },
      );

    this.messageDestroyedSubscription$ =
      this.notificationService.messageDestroyed$.subscribe(
        (message: NotificationMessage) => {
          const i = this.messages.indexOf(message);
          this.messages.splice(i, 1);
        },
      );
  }
  public ngOnDestroy(): void {
    this.messageAnnouncedSubscription$.unsubscribe();
    this.messageDestroyedSubscription$.unsubscribe();
  }
}
