import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import {
    HttpClient,
    HttpErrorResponse,
    HttpHeaders
} from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, EMPTY, map, of, tap } from 'rxjs';
import { ToastService } from './toast.service';
import { Account, EditPersonalData, OwnAccount } from '../model/account';
import { ResponseMessage } from '../common/response-message.enum';
import { AccessType } from '../model/access-type';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    ifMatch = '';

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
                    this.router.navigate(['/login']).then(() => {
                        this.toastService.clearAll();
                        this.toastService.showSuccess(
                            'toast.account.reset-password-message'
                        );
                    });
                }),
                catchError((response: HttpErrorResponse) => {
                    if (response.status == 404) {
                        this.router.navigate(['/login']).then(() => {
                            this.toastService.clearAll();
                            this.toastService.showSuccess(
                                'toast.account.reset-password-message'
                            );
                        });
                    } else {
                        this.toastService.showDanger(
                            'toast.account.reset-password-message-fail'
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
                            'toast.account.reset-password-change'
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
                        'toast.account.force-password-change-message'
                    );
                }),
                catchError((response: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.reset-password-fail',
                        'force-password-change',
                        response
                    );
                    return EMPTY;
                })
            )
            .subscribe();
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
                            'toast.account.force-password-change-override-message'
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

    getOwnProfile() {
        return this.http
            .get<OwnAccount>(`${environment.apiUrl}/accounts/me`, {
                observe: 'response'
            })
            .pipe(
                map((response) => {
                    this.ifMatch = response.headers.get('ETag') ?? '';
                    return response.body;
                })
            );
    }

    getProfile(id: number) {
        return this.http
            .get<Account>(`${environment.apiUrl}/accounts/${id}`, {
                observe: 'response'
            })
            .pipe(
                map((response) => {
                    this.ifMatch = response.headers.get('ETag') ?? '';
                    return response.body;
                })
            );
    }

    editOwnProfile(dto: EditPersonalData) {
        return this.http
            .put<OwnAccount>(`${environment.apiUrl}/accounts/me`, dto, {
                headers: new HttpHeaders({ 'If-Match': this.ifMatch }),
                observe: 'response'
            })
            .pipe(
                tap(() => {
                    this.toastService.showSuccess('SUCCESS!');
                }),
                map(() => true),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.edit-own-account-fail',
                        'edit-own-profile',
                        err
                    );
                    switch (err.error.message) {
                        case ResponseMessage.OPTIMISTIC_LOCK:
                            return of(true);
                        case ResponseMessage.SIGNATURE_MISMATCH:
                            return of(true);
                    }
                    return of(false);
                })
            );
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
            this.toastService.showDanger(genericMessageKey);
        } else {
            this.toastService.showDanger(method + '.' + response.error.message);
        }
    }
}
