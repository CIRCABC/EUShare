import { Component, OnInit, forwardRef, ElementRef, ViewChild, Self } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR, NG_VALIDATORS, NgControl, Validators, ValidationErrors, ValidatorFn } from '@angular/forms';

@Component({
  selector: 'app-email-input',
  templateUrl: './email-input.component.html',
  styleUrls: ['./email-input.component.css'],
})
export class EmailInputComponent implements ControlValueAccessor {
  private emailRegex = '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,4}$';
  onChange!: () => void;
  onTouched!: () => void;

  @ViewChild("input") input!: ElementRef;
  disabled!: boolean;

  constructor(@Self() public controlDirective: NgControl) {
    controlDirective.valueAccessor = this;
  }

  ngOnInit(): void {
    const control = this.controlDirective.control;
    if (control) {
      if (!control.validator) {
        control.setValidators([Validators.pattern(this.emailRegex)]);
      } else {
        control.setValidators([control.validator, Validators.pattern(this.emailRegex)])
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
    if (this.controlDirective.control && this.controlDirective.control.errors && this.controlDirective.control.errors.pattern) {
      return "Invalid email form !"
    }
    return null;
  }
}

