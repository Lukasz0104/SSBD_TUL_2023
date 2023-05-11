import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AccountService } from '../../services/account.service';
import { ToastService } from '../../services/toast.service';
import { environment } from '../../../environments/environment';

@Component({
    selector: 'app-change-password',
    templateUrl: './change-password.component.html'
})
export class ChangePasswordComponent {
    loading = false;
    showPassword = false;
    showRepeatPassword = false;
    showOldPassword = false;
    regexp = environment.passwordRegex;

    validators = [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(this.regexp)
    ];

    changePasswdForm: FormGroup = new FormGroup({
        oldPassword: new FormControl('', Validators.required),
        password: new FormControl('', this.validators),
        repeatPassword: new FormControl('', this.validators)
    });

    constructor(
        private accountService: AccountService,
        private toastService: ToastService
    ) {}

    get password() {
        return this.changePasswdForm.get('password');
    }

    get repeatPassword() {
        return this.changePasswdForm.get('repeatPassword');
    }

    get oldPassword() {
        return this.changePasswdForm.get('oldPassword');
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
                newPassword: password,
                oldPassword: oldPassword
            };
            this.accountService.changePassword(dto).subscribe({
                next: (res: boolean): void => {
                    if (res) {
                        this.changePasswdForm.reset();
                    }
                },
                error: (err) => this.toastService.showWarning(err)
            });
            this.loading = false;
        }
    }
}
