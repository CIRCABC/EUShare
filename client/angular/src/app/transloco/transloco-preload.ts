/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { APP_INITIALIZER } from '@angular/core';

import { TranslocoService } from '@ngneat/transloco';
import { firstValueFrom } from 'rxjs';

export function preloadEnglishTranslation(transloco: TranslocoService) {
  return function () {
    transloco.setActiveLang('en');
    return firstValueFrom(transloco.load('en'));
  };
}

export const preLoad = {
  provide: APP_INITIALIZER,
  multi: true,
  useFactory: preloadEnglishTranslation,
  deps: [TranslocoService],
};
