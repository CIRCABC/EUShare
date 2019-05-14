/*
EasyShare - a module of CIRCABC
Copyright (C) 2019 European Commission

This file is part of the "EasyShare" project.

This code is publicly distributed under the terms of EUPL-V1.2 license,
available at root of the project or at https://joinup.ec.europa.eu/collection/eupl/eupl-text-11-12.
*/
import { ValidatorFn, AbstractControl, FormArray } from '@angular/forms';
import { InterestGroup } from '../interfaces/interest-group';

// SOURCE VALIDATION
export function sourceValidator(allowedValues: Array<string>): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
        const source = control.value;
        const forbidden = !allowedValues.includes(source);
        if(forbidden) {
            console.log('forbiddenSource!')
        }
        return forbidden ? { 'forbiddenSource': { value: control.value } } : null;
    };
}
// FILE VALIDATION
export function customFileValidator(notMoreThanInBytes: number): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
        const file: File = control.value;
        if (file) {
            const forbidden = (file.size >= notMoreThanInBytes);
            if (forbidden) {
                console.log('forbiddenFileSize !' + file.size + '>' + notMoreThanInBytes);
            }
            return forbidden ? { 'forbiddenFileSize': { value: notMoreThanInBytes } } : null;
        }
        return null;
    };
}
// INTEREST GROUP VALIDATION
export function interestGroupValidator(validInterestGroups?: InterestGroup[]): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
        const ig: InterestGroup = control.value;
        console.log('interestGroupValidation : ' + ig + ' ' + validInterestGroups);
        if (validInterestGroups) {
            const forbidden = validInterestGroups.includes(control.value);
            if (forbidden) {
                console.log('forbiddenIG !');
            }
            return forbidden ? { 'forbiddenIG': { value: control.value } } : null;
        }
        return null;
    };
}

// MESSAGE TO RECIPIENT VALIDATION
export function messageToRecipientValidator(maxCharacters: number): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
        const message: string = control.value;
        if (message) {
            const forbidden = message.length > maxCharacters;
            if(forbidden) {
                console.log('forbiddenMessageLength!');
            }
            return forbidden ? { 'forbiddenMessageLength': { value: maxCharacters } } : null;
        }
        return null;
    };
}


// GLOBAL VALIDATION
export function globalValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
        const selectImport = control.get('selectImport');
        if (selectImport && selectImport.value === 'IG') {
            const selectInterestGroup = control.get('selectInterestGroup');
            if (selectInterestGroup && selectInterestGroup.value) {
                console.log('one interest group is selected ' + JSON.stringify(selectInterestGroup.value));
            } else {
                console.log('no interest group is selected');
                return { 'undefinedIG': { value: true } };
            }
        } else {
            if (selectImport && selectImport.value === 'DISK') {
                const selectFileFromDisk = control.get('fileFromDisk');
                if (selectFileFromDisk && selectFileFromDisk.value) {
                    console.log('one file from disk is selected ' + JSON.stringify(selectFileFromDisk.value));
                } else {
                    console.log('no file from disk is selected');
                    return { 'noFileSelected': { value: true } };
                }
            }
        }
        const emailOrLink = control.get('emailOrLink');
        if (emailOrLink && emailOrLink.value === 'Email') {
            const emailsWithMessages = <FormArray> control.get('emailsWithMessages');
            console.log('number of emailsWithMessages' + emailsWithMessages.length);
            if (emailsWithMessages.length === 0) {
                console.log('emptyRecipientList !');
                return { 'emptyRecipientList': { value: true } };
            }
        }

        return null;
    };
}
