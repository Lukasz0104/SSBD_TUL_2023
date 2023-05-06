import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const loginPasswordValidator: ValidatorFn = (
    control: AbstractControl
): ValidationErrors | null => {
    const password = control.value;
    if (password) {
        return null;
    }
    return { passwordRequired: true };
};
