import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

const passwordRegex =
    /^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[-!_$@?()[\]#%])[A-Za-z0-9\-!_$@?()[\]#%]{8,}$/;

export const strongPasswordValidator: ValidatorFn = (
    passwordControl: AbstractControl
): ValidationErrors | null => {
    const password = passwordControl.value;

    return passwordRegex.test(password) ? null : { weakPassword: true };
};
