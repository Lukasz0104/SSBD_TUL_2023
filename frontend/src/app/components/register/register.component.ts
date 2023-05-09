import { Component } from '@angular/core';
import {
    AbstractControl,
    FormControl,
    FormGroup,
    Validators
} from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { repeatPasswordValidator } from '../../validators/repeat-password.validator';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styles: [
        `
            label.form-label::after {
                content: '*';
            }
        `
    ]
})
export class RegisterComponent {
    protected languages = ['EN', 'PL'];

    protected personalDetailsForm = new FormGroup({
        firstName: new FormControl('', [Validators.required]),
        lastName: new FormControl('', [Validators.required]),
        language: new FormControl(this.languages[0], [Validators.required])
    });

    protected authDetailsForm = new FormGroup(
        {
            email: new FormControl('', [Validators.required, Validators.email]),
            login: new FormControl('', [Validators.required]),
            password: new FormControl('', [Validators.required]),
            repeatPassword: new FormControl('', [Validators.required])
        },
        { validators: repeatPasswordValidator }
    );

    protected ownerOrManagerForm = new FormGroup({
        address: new FormGroup({
            postalCode: new FormControl('', [Validators.required]),
            city: new FormControl('', [Validators.required]),
            street: new FormControl('', [Validators.required]),
            buildingNumber: new FormControl(0, [Validators.min(1)])
        }),
        licenseNumber: new FormControl('', [Validators.required])
    });

    protected readonly forms: FormGroup[] = [
        this.personalDetailsForm,
        this.authDetailsForm,
        this.ownerOrManagerForm
    ];

    protected formIndex = 0;

    constructor(private authService: AuthService) {}

    protected continueToOwnerForm() {
        this.licenseNumberControl.disable();
        this.formIndex++;
    }

    protected continueToManagerForm() {
        this.licenseNumberControl.enable();
        this.formIndex++;
    }

    protected get currentForm() {
        return this.forms[this.formIndex];
    }

    protected get progressPercentage() {
        const currentForm = this.currentForm;
        const controls = Object.values(currentForm.controls);
        const invalidCount = this.countInvalidFields(controls);
        return (
            (100 / this.forms.length) *
            (this.formIndex + 1 - invalidCount / this.countAllFields(controls))
        );
    }

    private countAllFields(controls: AbstractControl[]): number {
        let count = 0;
        for (const control of controls) {
            if ('controls' in control) {
                count += this.countAllFields(
                    Object.values(control.controls as AbstractControl[])
                );
            } else {
                count++;
            }
        }
        return count;
    }

    private countInvalidFields(controls: AbstractControl[]) {
        let count = 0;
        for (const control of controls) {
            if (control.errors) {
                count++;
            } else if ('controls' in control) {
                count += this.countInvalidFields(
                    Object.values(control.controls as AbstractControl[])
                );
            }
        }
        return count;
    }

    protected get allFormsValid() {
        return this.forms.every((f) => f.valid);
    }

    //#region control getters
    protected get emailControl() {
        return this.authDetailsForm.controls.email;
    }

    protected get loginControl() {
        return this.authDetailsForm.controls.login;
    }

    protected get passwordControl() {
        return this.authDetailsForm.controls.password;
    }

    protected get repeatPasswordControl() {
        return this.authDetailsForm.controls.repeatPassword;
    }

    protected get firstNameControl() {
        return this.personalDetailsForm.controls.firstName;
    }

    protected get lastNameControl() {
        return this.personalDetailsForm.controls.lastName;
    }

    protected get languageControl() {
        return this.personalDetailsForm.controls.language;
    }

    protected get postalCodeControl() {
        return this.ownerOrManagerForm.controls.address.controls.postalCode;
    }

    protected get citControl() {
        return this.ownerOrManagerForm.controls.address.controls.city;
    }

    protected get streetControl() {
        return this.ownerOrManagerForm.controls.address.controls.street;
    }

    protected get buildingNumberControl() {
        return this.ownerOrManagerForm.controls.address.controls.buildingNumber;
    }

    protected get licenseNumberControl() {
        return this.ownerOrManagerForm.controls.licenseNumber;
    }

    protected get passwordInvalid() {
        return this.passwordControl.invalid || this.passwordMismatch;
    }

    protected get repeatPasswordInvalid() {
        return this.repeatPasswordControl.invalid || this.passwordMismatch;
    }

    protected get passwordMismatch() {
        return this.authDetailsForm.errors?.['passwordMismatch'];
    }
    //#endregion
}
