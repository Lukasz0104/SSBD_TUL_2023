import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, EMPTY, map, of } from 'rxjs';
import { ToastService } from './toast.service';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    constructor(
        private http: HttpClient,
        private router: Router,
        private toastService: ToastService,
        private translate: TranslateService
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
                        this.translate.instant(
                            'toast.account.reset-password-message'
                        )
                    );
                }),
                catchError((response: HttpErrorResponse) =>
                    of(this.handleResetPasswordError(response))
                )
            );
    }

    changeLanguage(language: string) {
        return this.http
            .put(
                `${environment.apiUrl}/accounts/me/change-language/` + language,
                {}
            )
            .subscribe();
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
                        this.translate.instant(
                            'toast.account.reset-password-change'
                        )
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
