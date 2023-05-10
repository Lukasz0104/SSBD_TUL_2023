import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from '../../services/account.service';

@Component({
    selector: 'app-change-password',
    templateUrl: './change-password.component.html'
})
export class ChangePasswordComponent {
    loading = false;
    showPassword = false;
    showRepeatPassword = false;
    showOldPassword = false;
    regexp = new RegExp(
        '^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$'
    );
    validators = [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(this.regexp)
    ];

    changePasswdForm = new FormGroup({
        oldPassword: new FormControl('', this.validators),
        password: new FormControl('', this.validators),
        repeatPassword: new FormControl('', this.validators)
    });

    constructor(
        private accountService: AccountService,
        private route: ActivatedRoute
    ) {}

    get password() {
        return this.changePasswdForm.get('password');
    }

    get repeatPassword() {
        return this.changePasswdForm.get('repeatPassword');
    }

    onSubmit() {
        this.loading = true;
        const password: string =
            this.changePasswdForm.getRawValue().password ?? '';
        const repeatPassword: string =
            this.changePasswdForm.getRawValue().repeatPassword ?? '';
        const oldPassword: string =
            this.changePasswdForm.getRawValue().oldPassword ?? '';

        if (password !== repeatPassword) {
            return;
        } else if (password === oldPassword) {
            return;
        }

        if (this.changePasswdForm.valid) {
            const dto: object = {
                password,
                oldPassword
            };
            this.accountService.changePassword(dto);
        }
    }
}
