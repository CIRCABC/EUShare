/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import {
  Component,
  ViewChild,
  ElementRef,
  Self,
  Optional,
  OnInit,
} from '@angular/core';
import {
  ControlValueAccessor,
  NgControl,
  ValidatorFn,
  AbstractControl,
} from '@angular/forms';

@Component({
  selector: 'app-link-input',
  templateUrl: './link-input.component.html',
  styleUrls: ['./link-input.component.scss'],
})
export class LinkInputComponent implements ControlValueAccessor, OnInit {
  onChange!: (_: any) => void;
  onTouched!: () => void;

  @ViewChild('input', { static: true }) input!: ElementRef;
  disabled!: boolean;

  constructor(@Optional() @Self() public controlDirective: NgControl) {
    controlDirective.valueAccessor = this;
  }

  ngOnInit(): void {
    const control = this.controlDirective.control;
    if (control) {
      if (control.validator) {
        control.setValidators([control.validator, this.linkValidator(1)]);
      } else {
        control.setValidators([this.linkValidator(1)]);
      }
      setTimeout(() => control.updateValueAndValidity({ emitEvent: true }));
    }
  }

  writeValue(value: any): void {
    this.input.nativeElement.value = value;
  }
  registerOnChange(fn: () => void): void {
    this.onChange = fn;
  }
  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }
  setDisabledState(disabled: boolean) {
    this.disabled = disabled;
  }

  get errorMessage(): string | null {
    if (
      this.controlDirective.control?.errors?.['forbiddenLinkLength']
    ) {
      return `Link should be bigger than ${this.controlDirective.control.errors['forbiddenLinkLength'].value}`;
    }
    return null;
  }

  public linkValidator(minCharacters: number): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const message: string = control.value;
      if (message) {
        const forbidden = message.length < minCharacters;
        return forbidden
          ? { forbiddenLinkLength: { value: minCharacters } }
          : null;
      }
      return null;
    };
  }
}
