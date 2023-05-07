import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, EMPTY, map, of } from 'rxjs';
import { ToastService } from './toast.service';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    constructor(
        private http: HttpClient,
        private router: Router,
        private toastService: ToastService
    ) {}

    resetPassword(email: string) {
        return this.http
            .post(
                `${environment.apiUrl}/accounts/reset-password-message?email=` +
                    email,
                {}
            )
            .pipe(
                map(() => {
                    this.router.navigate(['/login']);
                    this.toastService.showSuccess(
                        'Reset password message has been sent'
                    );
                }),
                catchError((response: HttpErrorResponse) =>
                    of(this.handleResetPasswordError(response))
                )
            );
    }

    resetPasswordConfirm(resetPasswordDTO: object) {
        return this.http
            .post(
                `${environment.apiUrl}/accounts/reset-password`,
                resetPasswordDTO
            )
            .pipe(
                map(() => {
                    this.router.navigate(['/login']);
                    this.toastService.showSuccess(
                        'Password has been changed. You may log in now.'
                    );
                }),
                catchError((response: HttpErrorResponse) => {
                    this.handleResetPasswordError(response);
                    this.router.navigate(['/']);
                    return EMPTY;
                })
            )
            .subscribe();
    }

    handleResetPasswordError(response: HttpErrorResponse) {
        this.toastService.showDanger(response.error.message);
    }
}
