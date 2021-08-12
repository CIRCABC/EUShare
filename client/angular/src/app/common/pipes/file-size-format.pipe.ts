/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'fileSizeFormat',
})
export class FileSizeFormatPipe implements PipeTransform {
  transform(value: number): string {
    if (isNaN(value)) {
      return ' ERRORINTHEVALUE';
    }
    if (value < 0) {
      return '0 Bytes';
    }
    if (value >= 1024) {
      const valueInKb = Math.floor(value / 1024);
      if (valueInKb >= 1024) {
        const valueInMb = Math.floor(valueInKb / 1024);
        if (valueInMb >= 1024) {
          const valueInGb = Math.floor(valueInMb / 1024);
          return valueInGb + ' GB';
        } else {
          return valueInMb + ' MB';
        }
      } else {
        return valueInKb + ' KB';
      }
    } else {
      return value + ' Bytes';
    }
  }
}
