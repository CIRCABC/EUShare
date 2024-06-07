/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'fileSizeFormat',
  standalone: true,
})

export class FileSizeFormatPipe implements PipeTransform {

  transform(value: number): string {
 
    if (!value && Number.isNaN(value)) {
      return 'ERRORINTHEVALUE';
    }

    if (value === 0) return '0 Bytes';

    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    let index = 0; 

    while (value >= 1024 && index < sizes.length - 1) {
      value /= 1024;
      index++;
    }

    return `${value.toFixed(2)} ${sizes[index]}`;
  }
}
