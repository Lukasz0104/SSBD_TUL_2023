import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

const decimalRegex = /^([0-9]+(\.[0-9]{1,2})?)$/;

export const twoDecimalPlacesValidator: ValidatorFn = (
    decimalControl: AbstractControl
): ValidationErrors | null => {
    const number = decimalControl.getRawValue();
    console.log(number + ' ' + decimalRegex.test(number));
    return decimalRegex.test(number) ? null : { invalidDecimal: true };
};
