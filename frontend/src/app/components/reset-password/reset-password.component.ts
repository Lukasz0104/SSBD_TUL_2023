import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AccountService } from '../../services/account.service';

@Component({
    selector: 'app-reset-password',
    templateUrl: './reset-password.component.html'
})
export class ResetPasswordComponent {
    resetPasswordForm = new FormGroup({
        email: new FormControl('', [Validators.required, Validators.email])
    });

    loading = false;

    constructor(private accountService: AccountService) {}

    get email() {
        return this.resetPasswordForm.get('email');
    }

    onSubmit() {
        if (this.resetPasswordForm.valid) {
            this.loading = true;
            const email = this.resetPasswordForm.getRawValue().email ?? '';
            this.accountService.resetPassword(email).subscribe(() => {
                this.loading = false;
            });
        }
    }
}
