/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Pipe, PipeTransform } from '@angular/core';
import { ReplaySubject } from 'rxjs';

@Pipe({
  name: 'fileDateFormat',
})
export class FileDateFormatPipe implements PipeTransform {
  transform(value: string): string {
    
      return value.toString().replace(new RegExp(",", 'g'),"-");
    
  }
}
