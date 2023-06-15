import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const effectiveDateValidator: ValidatorFn = (
    control: AbstractControl
): ValidationErrors | null => {
    const date = control.getRawValue();
    return date.day == 1 ? null : { invalidDate: true };
};
