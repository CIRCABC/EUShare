import { Component, OnDestroy } from '@angular/core';
import { NotificationMessage } from './notification-message';
import { NotificationService } from './notification.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-notification-system',
  templateUrl: './notification-system.component.html',
  styleUrls: ['./notification-system.component.css']
})
export class NotificationSystemComponent implements OnDestroy {
  public messages: NotificationMessage[] = [];
  private messageAnnouncedSubscription$: Subscription;
  private messageDestroyedSubscription$: Subscription;

  public constructor(private notificationService: NotificationService) {
    this.messageAnnouncedSubscription$ = this.notificationService.messageAnnounced$.subscribe(
      (message: NotificationMessage) => {
        this.messages.push(message);
      }
    );

    this.messageDestroyedSubscription$ = this.notificationService.messageDestroyed$.subscribe(
      (message: NotificationMessage) => {
        const i = this.messages.indexOf(message);
        this.messages.splice(i, 1);
      }
    );
  }
  public ngOnDestroy(): void {
    this.messageAnnouncedSubscription$.unsubscribe();
    this.messageDestroyedSubscription$.unsubscribe();
  }
}
