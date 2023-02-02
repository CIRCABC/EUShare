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
