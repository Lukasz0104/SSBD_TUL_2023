import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css'],
})
export class LoginComponent {
    loginForm = new FormGroup({
        login: new FormControl('', [Validators.required]),
        password: new FormControl('', [Validators.required]),
    });

    constructor(private authService: AuthService, private router: Router) {}

    onSubmit() {
        if (this.loginForm.valid) {
            const username = this.loginForm.getRawValue().login ?? '';
            const password = this.loginForm.getRawValue().password ?? '';

            this.authService
                .login(username.toString(), password.toString())
                .subscribe(
                    (result) => {
                        if (result.status == 200) {
                            this.authService.saveUserData(result);
                            this.authService.authenticated.next(true);
                            this.router.navigate(['/dashboard']);
                        }
                    },
                    () => {
                        this.authService.authenticated.next(false);
                        this.clearPassword();
                    }
                );
        }
    }

    clearPassword() {
        this.loginForm.get('password')?.reset();
    }
}
