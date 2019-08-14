/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { ValidatorFn, AbstractControl, FormArray } from '@angular/forms';

// GLOBAL VALIDATION
export function globalValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
        const selectFileFromDisk = control.get('fileFromDisk');
        if (selectFileFromDisk && selectFileFromDisk.value) {
        } else {
            return { 'noFileSelected': { value: true } };
        }
        const emailOrLink = control.get('emailOrLink');
        if (emailOrLink && emailOrLink.value === 'Email') {
            const emailsWithMessages = <FormArray>control.get('emailsWithMessages');
            if (emailsWithMessages.length === 0) {
                return { 'emptyRecipientList': { value: true } };
            }
        }
        if (emailOrLink && emailOrLink.value === 'Link') {
            const namesOnly = <FormArray>control.get('namesOnly');
            if (namesOnly.length === 0) {
                return { 'emptyRecipientList': { value: true } };
            }
        }
        return null;
    };
}
