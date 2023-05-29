import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const diffPasswordValidator: ValidatorFn = (
    control: AbstractControl
): ValidationErrors | null => {
    const password = control.get('password');
    const oldPassword = control.get('oldPassword');

    return password && oldPassword && password.value !== oldPassword.value
        ? null
        : { samePassword: true };
};
