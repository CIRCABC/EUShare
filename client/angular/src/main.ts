import { enableProdMode, importProvidersFrom } from '@angular/core';

import { environment } from './environments/environment';
import { AppComponent } from './app/app.component';
import { NgChartsModule } from 'ng2-charts';
import { provideAnimations } from '@angular/platform-browser/animations';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { OAuthModule } from 'angular-oauth2-oidc';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import {
  HammerModule,
  BrowserModule,
  bootstrapApplication,
} from '@angular/platform-browser';
import { TranslocoRootModule } from './app/transloco/transloco-root.module';
import { TranslocoModule } from '@ngneat/transloco';
import { ApiModule } from './app/openapi/api.module';
import { AppRoutingModule } from './app/app-routing.module';
import { preLoad } from './app/transloco/transloco-preload';
import { FileSizeFormatPipe } from './app/common/pipes/file-size-format.pipe';
import { HttpErrorInterceptor } from './app/interceptors/http-error-interceptor';
import { BasicAuthenticationInterceptor } from './app/interceptors/basic-authentication-interceptor';
import {
  HTTP_INTERCEPTORS,
  withInterceptorsFromDi,
  provideHttpClient,
} from '@angular/common/http';
import { BASE_PATH } from './app/openapi';
import { APP_BASE_HREF } from '@angular/common';
import { KeyStoreService } from './app/services/key-store.service';
import 'hammerjs';

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(
      AppRoutingModule,
      ApiModule,
      TranslocoModule,
      TranslocoRootModule,
      HammerModule,
      BrowserModule,
      ReactiveFormsModule,
      FormsModule,
      OAuthModule.forRoot({
        resourceServer: {
          allowedUrls: ['https://localhost:8888'],
          sendAccessToken: true,
        },
      }),
      FontAwesomeModule,
      NgChartsModule
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
    FileSizeFormatPipe,
    preLoad,
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimations(),
  ],
}).catch((err) => console.log(err));
