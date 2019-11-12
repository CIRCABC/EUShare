import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { SessionService, UsersService } from '../openapi';
import { NotificationService } from '../common/notification/notification.service';
import { delay } from 'rxjs/operators';

@Component({
  selector: 'app-call-back',
  templateUrl: './call-back.component.html',
  styleUrls: ['./call-back.component.scss']
})
export class CallBackComponent implements OnInit {

  constructor(private userService: UsersService, private notificationService: NotificationService, private api: SessionService, private oAuthService: OAuthService, private router: Router) { }

  async ngOnInit() {
    this.oAuthService.events.subscribe(next => {
      const nextType: string = next.type;
      switch (nextType) {
        case 'token_expires':
          {
            this.notificationService.addErrorMessage('Encountered an OIDC error of type ' + next.type);
            this.oAuthService.silentRefresh().then(()=> {
              this.notificationService.addSuccessMessage('Refreshed authentication token');
            }).catch(() => {
              this.notificationService.addErrorMessage('Authentication token could not be refreshed');
            });
            break;
          }
        case 'token_received':
        case 'token_refreshed':
        case 'silently_refreshed':
          {
            this.notificationService.addSuccessMessage('Received valid token');
            this.loginAndRedirect();
            break;
          }
        case 'discovery_document_loaded':
        case 'logout':
          {
            // nothing
            break;
          }
        default: {
          this.notificationService.addErrorMessage('Encountered an OIDC error of type ' + next.type);
          break;
        }
      }
    });
  }

  public async loginAndRedirect() {
    try {
      const identifier = await this.api.postLogin().toPromise();
      const userInfo = await this.userService.getUserUserInfo(identifier).toPromise();
      this.api.setStoredUserInfo(userInfo);
      this.router.navigateByUrl('home');
    } catch (e) {
      console.log(e);
      this.notificationService.errorMessageToDisplay(
        e,
        'loading your user information'
      );
    }
  }

}
