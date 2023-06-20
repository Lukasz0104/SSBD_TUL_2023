import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const postalCodeValidator: ValidatorFn = (
    postalCodeControl: AbstractControl
): ValidationErrors | null => {
    const postalCode: string = postalCodeControl.value;

    return postalCode && postalCode.match(/^\d{2}-\d{3}$/)
        ? null
        : { postalCode: 'validation.postal-code.pattern' };
};
