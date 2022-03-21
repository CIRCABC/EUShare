/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Injectable } from '@angular/core';
import { getBrowserLang, HashMap, TranslocoService } from '@ngneat/transloco';

@Injectable({
  providedIn: 'root',
})
export class I18nService {
  constructor(private translateService: TranslocoService) {}

  public configureI18n() {
    // this language will be used as a fallback when a translation isn't found in the current language
   // this.translateService.setDefaultLang('en');

    // the lang to use, if the lang isn't available, it will use the language from the browser
    // if the browser language could not be read, it will use english as language
    /*const browserLang = getBrowserLang();
    if (browserLang !== undefined) {
      this.translateService.setActiveLang(browserLang);
    } else {
      this.translateService.setActiveLang('en');
    }*/
  }
  public translate(key: string, params?: HashMap): string {
    if (params === undefined) {
      return this.translateService.translate<string>(key);
    } else {
      return this.translateService.translate<string>(key, params);
    }
  }
  public contactSupport(): string {
    return this.translate('contact.support');
  }

  public whileTrying(): string {
    return this.translate('while.trying');
  }

  public to(): string {
    return this.translate('to');
  }
}
