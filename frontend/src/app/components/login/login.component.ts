import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html'
})
export class LoginComponent {
    loginForm = new FormGroup({
        login: new FormControl('', [Validators.required]),
        password: new FormControl('', [Validators.required])
    });

    get login() {
        return this.loginForm.get('login');
    }

    get password() {
        return this.loginForm.get('password');
    }

    loading = false;

    constructor(
        private authService: AuthService,
        private toastService: ToastService
    ) {}

    onSubmit() {
        if (this.loginForm.valid) {
            this.loading = true;
            const username = this.loginForm.getRawValue().login ?? '';
            const password = this.loginForm.getRawValue().password ?? '';

            this.authService
                .login(username, password)
                .subscribe((result: boolean) => {
                    this.loading = false;
                    if (!result) {
                        this.toastService.showDanger('Login unsuccessful');
                        this.clearPassword();
                    }
                });
        }
    }

    clearPassword() {
        this.loginForm.get('password')?.reset();
    }
}
