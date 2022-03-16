/*
CIRCABC Share - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "CIRCABC Share" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/

import {
  Component,
  ElementRef,
  ViewChild,
  Self,
  Optional,
  OnInit,
  Output,
  EventEmitter,
  Input,
} from '@angular/core';
import { ControlValueAccessor, NgControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-email-input',
  templateUrl: './email-input.component.html',
  styleUrls: ['./email-input.component.scss'],
})
export class EmailInputComponent implements ControlValueAccessor, OnInit {
  private emailRegex = '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,10}$';
  onChange!: (_: any) => void;
  onTouched!: () => void;

  // eslint-disable-next-line @angular-eslint/no-input-rename
  @Input('showAddRecipient')
  public showAddRecipient = true;

  @ViewChild('input', { static: true }) input!: ElementRef;

  // eslint-disable-next-line @angular-eslint/no-output-rename
  @Output('downClick')
  clicked = new EventEmitter<boolean>();

  disabled!: boolean;

  constructor(@Optional() @Self() public controlDirective: NgControl) {
    controlDirective.valueAccessor = this;
  }

  ngOnInit(): void {
    const control = this.controlDirective.control;
    if (control) {
      if (!control.validator) {
        control.setValidators([Validators.pattern(this.emailRegex)]);
      } else {
        control.setValidators([
          control.validator,
          Validators.pattern(this.emailRegex),
        ]);
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
      this.controlDirective.control &&
      this.controlDirective.control.errors &&
      this.controlDirective.control.errors['pattern']
    ) {
      return 'Invalid email form';
    }
    return null;
  }

  addRecipient() {
    if (this.errorMessage === null && this.input.nativeElement !== '') {
      this.clicked.emit(true);
    }
  }
}
