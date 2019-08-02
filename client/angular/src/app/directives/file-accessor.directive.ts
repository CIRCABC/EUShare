/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { Directive } from '@angular/core';
import {NG_VALUE_ACCESSOR, ControlValueAccessor} from "@angular/forms";

@Directive({
  selector: "input[type=file]",
    host : {
        "(change)" : "onChange($event.target.files[0])",
        "(blur)": "onTouched()"
    },
    providers: [
        { provide: NG_VALUE_ACCESSOR, useExisting: FileAccessorDirective, multi: true }
    ]
})
export class FileAccessorDirective implements ControlValueAccessor {
  value: any;
  onChange = (value:any) => {};
  onTouched = () => {};
  writeValue(value: any) {}
  registerOnChange(fn: any) { this.onChange = fn; }
  registerOnTouched(fn: any) { this.onTouched = fn; }
}
