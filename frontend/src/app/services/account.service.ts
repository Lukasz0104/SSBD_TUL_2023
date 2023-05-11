import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, EMPTY, map, of } from 'rxjs';
import { ToastService } from './toast.service';
import { TranslateService } from '@ngx-translate/core';
import { Account } from '../model/account';
import { AccessType } from '../model/access-type';

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
                        this.router.navigate(['/login']).then(() => {
                            this.toastService.clearAll();
                            this.toastService.showSuccess(
                                this.translate.instant(
                                    'toast.account.reset-password-message'
                                )
                            );
                        });
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
                    this.handleError(
                        'toast.account.reset-password-fail',
                        'password-change',
                        response
                    );
                    this.router.navigate(['/']);
                    return EMPTY;
                })
            )
            .subscribe();
    }

    forcePasswordChange(login: string) {
        return this.http
            .put(
                `${environment.apiUrl}/accounts/force-password-change/` + login,
                {}
            )
            .pipe(
                map(() => {
                    this.toastService.clearAll();
                    this.toastService.showSuccess(
                        this.translate.instant(
                            'toast.account.force-password-change-message'
                        )
                    );
                }),
                catchError((response: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.reset-password-fail',
                        'force-password-change',
                        response
                    );
                    return of(null);
                })
            );
    }

    overrideForcePasswordChange(resetPasswordDTO: object) {
        return this.http
            .put(
                `${environment.apiUrl}/accounts/override-forced-password`,
                resetPasswordDTO
            )
            .pipe(
                map(() => {
                    this.router.navigate(['/login']).then(() => {
                        this.toastService.clearAll();
                        this.toastService.showSuccess(
                            this.translate.instant(
                                'force-password-change-override-message'
                            )
                        );
                    });
                }),
                catchError((response: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.reset-password-fail',
                        'password-change',
                        response
                    );
                    this.router.navigate(['/']);
                    return EMPTY;
                })
            )
            .subscribe();
    }

    getAccountsByTypeAndActive(type: AccessType, active: boolean) {
        let url;
        switch (type) {
            case AccessType.OWNER:
                url = 'accounts/owners';
                break;
            case AccessType.MANAGER:
                url = 'accounts/managers';
                break;
            case AccessType.ADMIN:
                url = 'accounts/admins';
                break;
            default:
                url = 'accounts';
                break;
        }
        return this.http.get<Account[]>(
            `${environment.apiUrl}/${url}?active=${active}`
        );
    }

    getUnapprovedAccountsByType(type: AccessType) {
        if (type == AccessType.OWNER) {
            return this.http.get<Account[]>(
                `${environment.apiUrl}/accounts/owners/unapproved`
            );
        } else if (type == AccessType.MANAGER) {
            return this.http.get<Account[]>(
                `${environment.apiUrl}/accounts/managers/unapproved`
            );
        }
        return of([]);
    }

    handleError(
        genericMessageKey: string,
        method: string,
        response: HttpErrorResponse
    ) {
        if (response.status == 500 || response.error.message == null) {
            this.toastService.showDanger(
                this.translate.instant(genericMessageKey)
            );
        } else {
            this.toastService.showDanger(
                this.translate.instant(method + '.' + response.error.message)
            );
        }
    }
}
