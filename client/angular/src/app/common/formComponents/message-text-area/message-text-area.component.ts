/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import {
  Component,
  ViewChild,
  ElementRef,
  Self,
  Optional,
  OnInit
} from '@angular/core';
import {
  ControlValueAccessor,
  NgControl,
  ValidatorFn,
  AbstractControl
} from '@angular/forms';

@Component({
  selector: 'app-message-text-area',
  templateUrl: './message-text-area.component.html',
  styleUrls: ['./message-text-area.component.css']
})
export class MessageTextAreaComponent implements ControlValueAccessor, OnInit {
  onChange!: (_: any) => void;
  onTouched!: () => void;
  @ViewChild('textarea', { static: true }) textarea!: ElementRef;
  disabled!: boolean;

  constructor(@Optional() @Self() public controlDirective: NgControl) {
    controlDirective.valueAccessor = this;
  }

  ngOnInit(): void {
    const control = this.controlDirective.control;
    if (control) {
      if (!control.validator) {
        control.setValidators([this.messageToRecipientValidator(200)]);
      } else {
        control.setValidators([
          control.validator,
          this.messageToRecipientValidator(200)
        ]);
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

  public messageToRecipientValidator(maxCharacters: number): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const message: string = control.value;
      if (message) {
        const forbidden = message.length > maxCharacters;
        return forbidden
          ? { forbiddenMessageLength: { value: maxCharacters } }
          : null;
      }
      return null;
    };
  }

  get errorMessage(): string | null {
    if (
      this.controlDirective.control &&
      this.controlDirective.control.errors &&
      this.controlDirective.control.errors.forbiddenMessageLength
    ) {
      return (
        "Text shouldn't be bigger than " +
        this.controlDirective.control.errors.forbiddenMessageLength.value
      );
    }
    return null;
  }
}
