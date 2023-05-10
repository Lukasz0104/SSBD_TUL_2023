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
                    this.router.navigate(['/login']).then(() => {
                        this.toastService.clearAll();
                        this.toastService.showSuccess(
                            this.translate.instant(
                                'toast.account.reset-password-message'
                            )
                        );
                    });
                }),
                catchError((response: HttpErrorResponse) => {
                    if (response.status == 404) {
                        this.router.navigate(['/login']);
                        this.toastService.clearAll();
                        this.toastService.showSuccess(
                            this.translate.instant(
                                'toast.account.reset-password-message'
                            )
                        );
                    } else {
                        this.toastService.showDanger(
                            this.translate.instant(
                                'toast.account.reset-password-message-fail'
                            )
                        );
                    }
                    return of(null);
                })
            );
    }

    changeLanguage(language: string) {
        return this.http
            .put(
                `${environment.apiUrl}/accounts/me/change-language/` + language,
                {}
            )
            .pipe(
                map(() => {
                    return true;
                }),
                catchError(() => of(false))
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
                    this.router.navigate(['/login']).then(() => {
                        this.toastService.clearAll();
                        this.toastService.showSuccess(
                            this.translate.instant(
                                'toast.account.reset-password-change'
                            )
                        );
                    });
                }),
                catchError((response: HttpErrorResponse) => {
                    if (
                        response.status == 500 ||
                        response.error.message == undefined
                    ) {
                        this.toastService.showDanger(
                            this.translate.instant(
                                'toast.account.reset-password-fail'
                            )
                        );
                    } else {
                        this.toastService.showDanger(
                            this.translate.instant(response.error.message)
                        );
                    }
                    this.router.navigate(['/']);
                    return EMPTY;
                })
            )
            .subscribe();
    }

    forcePasswordChange(login: string) {
        return this.http.put(
            `${environment.apiUrl}/accounts/force-password-change/` + login,
            {}
        );
    }

    overrideForcePasswordChange(resetPasswordDTO: object) {
        return this.http
            .put(
                `${environment.apiUrl}/accounts/override-forced-password`,
                resetPasswordDTO
            )
            .pipe(
                catchError(() => {
                    return EMPTY;
                })
            );
    }
}
