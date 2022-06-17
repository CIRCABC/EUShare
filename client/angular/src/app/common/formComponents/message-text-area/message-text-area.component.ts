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
  Input,
} from '@angular/core';
import {
  ControlValueAccessor,
  NgControl,
  Validators,
} from '@angular/forms';
import { I18nService } from '../../i18n/i18n.service';

@Component({
  selector: 'app-message-text-area',
  templateUrl: './message-text-area.component.html',
  styleUrls: ['./message-text-area.component.scss'],
})
export class MessageTextAreaComponent implements ControlValueAccessor, OnInit {
  onChange!: (_: any) => void;
  onTouched!: () => void;
  @ViewChild('textarea', { static: true }) textarea!: ElementRef;
  disabled!: boolean;

  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('isOptional')
  public isOptional = true;

  constructor(
    @Optional() @Self() public controlDirective: NgControl,
    private i18nService: I18nService
  ) {
    controlDirective.valueAccessor = this;
  }

  ngOnInit(): void {
    const control = this.controlDirective.control;
    if (control) {
      if (!control.validator) {
        control.setValidators([Validators.maxLength(255)]);
      } else {
        control.setValidators([control.validator, Validators.maxLength(255)]);
      }
      setTimeout(() => control.updateValueAndValidity({ emitEvent: true }));
    }
  }

  writeValue(value: any): void {
    this.textarea.nativeElement.value = value;
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
      this.controlDirective.control &&
      this.controlDirective.control.errors &&
      this.controlDirective.control.errors['maxlength']
    ) {
      return this.i18nService.translate(
        'validation.maxlength',
        this.controlDirective.control.errors['maxlength']
      );
    }
    return null;
  }
}
