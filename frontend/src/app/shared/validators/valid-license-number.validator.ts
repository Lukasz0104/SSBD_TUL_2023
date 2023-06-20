import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

const licenseNumberRegex = /^\d{6,8}$/;

export const validLicenseNumberValidator: ValidatorFn = (
    licenseControl: AbstractControl
): ValidationErrors | null => {
    const licenseNumber = licenseControl.getRawValue();
    return licenseNumberRegex.test(licenseNumber)
        ? null
        : { invalidLicenseNumber: true };
};
