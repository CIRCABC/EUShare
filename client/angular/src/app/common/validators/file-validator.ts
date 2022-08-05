/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { ValidatorFn, AbstractControl } from '@angular/forms';
import { NotificationService } from '../notification/notification.service';

// FILE VALIDATION
export function fileSizeValidator(notMoreThanInBytes: number, notificationService: NotificationService, message: string): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    const file: File = control.value;
    if (file) {
      const forbidden = file.size >= notMoreThanInBytes;
      if (forbidden) {
        notificationService.addErrorMessage(
          message
        );
      }
      return forbidden
        ? { forbiddenFileSize: { value: notMoreThanInBytes } }
        : null;
    }
    return null;
  };
}
