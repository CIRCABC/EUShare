import { APP_BASE_HREF } from '@angular/common';
import {
  HTTP_INTERCEPTORS,
  HttpClient,
  HttpClientModule,
} from '@angular/common/http';
import { enableProdMode, importProvidersFrom, Injectable } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  bootstrapApplication,
  BrowserModule,
  HammerModule,
} from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import {
  Translation,
  TRANSLOCO_CONFIG,
  TRANSLOCO_LOADER,
  TranslocoLoader,
  TranslocoModule,
} from '@ngneat/transloco';
import { OAuthModule, OAuthModuleConfig } from 'angular-oauth2-oidc';
import 'hammerjs';
import { NgChartsModule } from 'ng2-charts';
import { AppRoutingModule } from './app/app-routing.module';
import { AppComponent } from './app/app.component';
import { BasicAuthenticationInterceptor } from './app/interceptors/basic-authentication-interceptor';
import { HttpErrorInterceptor } from './app/interceptors/http-error-interceptor';
import { BASE_PATH } from './app/openapi';
import { ApiModule } from './app/openapi/api.module';
import { KeyStoreService } from './app/services/key-store.service';
import { environment } from './environments/environment';
import { translocoConfig } from './configs/transloco.config';

@Injectable({ providedIn: 'root' })
export class HttpLoader implements TranslocoLoader {
  constructor(private http: HttpClient) {}

  getTranslation(langPath: string) {
    return this.http.get<Translation>(`/share/assets/i18n/${langPath}.json`);
  }
}

const oauthModuleConfig: OAuthModuleConfig = {
  resourceServer: {
    allowedUrls: ['https://localhost:8888'],
    sendAccessToken: true,
  },
};

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(
      OAuthModule.forRoot(oauthModuleConfig),
      BrowserModule,
      ReactiveFormsModule,
      FormsModule,

      FontAwesomeModule,
      NgChartsModule,
      HammerModule,
      ApiModule,
      AppRoutingModule,
      TranslocoModule,
      HttpClientModule
    ),
    KeyStoreService,
    { provide: APP_BASE_HREF, useValue: environment.frontend_url },
    { provide: BASE_PATH, useValue: environment.API_BASE_PATH },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: BasicAuthenticationInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true,
    },
    {
      provide: TRANSLOCO_LOADER,
      useClass: HttpLoader,
    },
    {
      provide: TRANSLOCO_CONFIG,
      useValue: translocoConfig, // Use the externalized Transloco configuration
    },
    provideAnimations(),
  ],
});
