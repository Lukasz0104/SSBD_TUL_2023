import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AccountService } from '../../../shared/services/account.service';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-force-password-change-override',
    templateUrl: './force-password-change-override.component.html'
})
export class ForcePasswordChangeOverrideComponent {
    regexp = new RegExp(
        '^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[\\-!_$@?()\\[\\]#%])[A-Za-z0-9\\-!_$@?()\\[\\]#%]{8,}$'
    );

    resetPasswordForm = new FormGroup({
        password: new FormControl('', [
            Validators.required,
            Validators.minLength(8),
            Validators.pattern(this.regexp)
        ]),
        repeatPassword: new FormControl('', [
            Validators.required,
            Validators.minLength(8)
        ])
    });

    loading = false;
    token = '';
    showPassword = false;
    showRepeatPassword = false;

    constructor(
        private accountService: AccountService,
        private route: ActivatedRoute
    ) {
        this.route.paramMap.subscribe((params) => {
            const param = params.get('token');
            if (param != null) {
                this.token = param.toString();
            }
        });
    }

    get password() {
        return this.resetPasswordForm.get('password');
    }

    get passwordControl() {
        return this.resetPasswordForm.controls.password;
    }

    get repeatPassword() {
        return this.resetPasswordForm.get('repeatPassword');
    }

    get repeatPasswordControl() {
        return this.resetPasswordForm.controls.repeatPassword;
    }

    onSubmit() {
        this.loading = true;
        const password = this.resetPasswordForm.getRawValue().password;
        const repeatPassword =
            this.resetPasswordForm.getRawValue().repeatPassword;

        if (password !== repeatPassword) {
            return;
        }

        if (this.resetPasswordForm.valid) {
            const resetPasswordDTO: object = {
                password: this.resetPasswordForm.getRawValue().password,
                token: this.token
            };
            this.accountService
                .overrideForcePasswordChange(resetPasswordDTO)
                .subscribe((result) => (this.loading = result ?? false));
        }
    }
}
