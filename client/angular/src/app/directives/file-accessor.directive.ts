/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import { Directive, HostListener } from '@angular/core';
import { NG_VALUE_ACCESSOR, ControlValueAccessor } from '@angular/forms';

@Directive({
  selector: '[appEsFileInput]',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: FileAccessorDirective,
      multi: true,
    },
  ],
})
export class FileAccessorDirective implements ControlValueAccessor {
  value: any;

  @HostListener('change', ['$event.target.files[0]'])
  onChange = (_value: any) => {}; // NOSONAR

  @HostListener('blur', [])
  onTouched = () => {}; // NOSONAR
  writeValue(_value: any) {} // NOSONAR
  registerOnChange(fn: any) {
    this.onChange = fn;
  }
  registerOnTouched(fn: any) {
    this.onTouched = fn;
  }
}
