/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { ValidatorFn, AbstractControl } from '@angular/forms';

export function recipientValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    const email = control.get('email');
    const message = control.get('message');

    if (!(email && email.value && message && message.value)) {
      return { recipientValidationError: { value: true } };
    }
    return null;
  };
}
