import { Component } from '@angular/core';
import {
    AbstractControl,
    FormGroup,
    NonNullableFormBuilder,
    Validators
} from '@angular/forms';
import { AccountService } from '../../services/account.service';
import { ToastService } from '../../services/toast.service';
import { repeatPasswordValidator } from '../../validators/repeat-password.validator';
import { strongPasswordValidator } from '../../validators/strong-password.validator';
import { environment } from '../../../environments/environment';

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
    captchaCode = '';

    protected languages = environment.languages;

    protected personalDetailsForm = this.fb.group({
        firstName: this.fb.control('', {
            validators: [Validators.required, Validators.maxLength(100)]
        }),
        lastName: this.fb.control('', {
            validators: [Validators.required, Validators.maxLength(100)]
        }),
        language: this.fb.control(environment.languages[0], {
            validators: [Validators.required]
        })
    });

    protected authDetailsForm = new FormGroup(
        {
            email: this.fb.control('', {
                validators: [
                    Validators.required,
                    Validators.email,
                    Validators.maxLength(320)
                ]
            }),
            login: this.fb.control('', {
                validators: [
                    Validators.required,
                    Validators.minLength(3),
                    Validators.maxLength(100)
                ]
            }),
            password: this.fb.control('', {
                validators: [Validators.required, strongPasswordValidator]
            }),
            repeatPassword: this.fb.control('', {
                validators: [Validators.required]
            })
        },
        { validators: repeatPasswordValidator }
    );

    protected ownerOrManagerForm = new FormGroup({
        address: this.fb.group({
            postalCode: this.fb.control('', {
                validators: [
                    Validators.required,
                    Validators.minLength(6),
                    Validators.maxLength(6)
                ]
            }),
            city: this.fb.control('', {
                validators: [
                    Validators.required,
                    Validators.minLength(2),
                    Validators.maxLength(85)
                ]
            }),
            street: this.fb.control('', {
                validators: [Validators.required, Validators.maxLength(85)]
            }),
            buildingNumber: this.fb.control(0, {
                validators: [Validators.required, Validators.min(1)]
            })
        }),
        licenseNumber: this.fb.control('', {
            validators: [Validators.required]
        })
    });

    protected readonly forms: FormGroup[] = [
        this.personalDetailsForm,
        this.authDetailsForm,
        this.ownerOrManagerForm
    ];

    protected formIndex = 0;

    constructor(
        private accountService: AccountService,
        private fb: NonNullableFormBuilder,
        private toast: ToastService
    ) {}

    register() {
        const { repeatPassword, ...dto } = {
            ...this.personalDetailsForm.getRawValue(),
            ...this.authDetailsForm.getRawValue(),
            address: this.ownerOrManagerForm.getRawValue().address,
            licenseNumber: this.ownerOrManagerForm.value.licenseNumber,
            captchaCode: this.captchaCode
        };

        this.accountService.register(dto).subscribe((response) => {
            if (response) {
                this.toast.showSuccess('toast.account.register');
                this.forms.forEach((f) => f.reset());
                this.formIndex = 0;
            }
        });
    }

    onCaptchaResolved(captcha: string) {
        this.captchaCode = captcha;
    }

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
        return (
            this.forms.every((f) => f.valid) &&
            this.captchaCode &&
            this.captchaCode.length > 0
        );
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

    protected get cityControl() {
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
