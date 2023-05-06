import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const loginValidator: ValidatorFn = (
    control: AbstractControl
): ValidationErrors | null => {
    const login = control.value;
    if (login) {
        return null;
    }
    return { loginRequired: true };
};
