import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import {
    HttpClient,
    HttpErrorResponse,
    HttpHeaders
} from '@angular/common/http';
import { Account, EditPersonalData, OwnAccount } from '../model/account';
import { catchError, map, of, tap } from 'rxjs';
import { ToastService } from './toast.service';
import { ResponseMessage } from '../common/response-message.enum';
import { AccessType } from '../model/access-type';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    ifMatch = '';

    constructor(private http: HttpClient, private toastService: ToastService) {}

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
                    this.toastService.showDanger(err.error.message);
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
        import { HttpClient } from '@angular/common/http';
        import { environment } from '../../environments/environment';
        import { ChangeEmailForm } from '../model/email-form';
        import { catchError, map, of } from 'rxjs';
        import { ActiveStatusDto } from '../model/active-status-dto';

        @Injectable({ providedIn: 'root' })
        export class AccountService {
            constructor(private http: HttpClient) {}

            changeEmail() {
                return this.http
                    .post(
                        `${environment.apiUrl}/accounts/me/change-email`,
                        null
                    )
                    .pipe(
                        map(() => {
                            return true;
                        }),
                        catchError(() => of(false))
                    );
            }

            confirmEmail(email: string, token: string) {
                return this.http
                    .put<ChangeEmailForm>(
                        `${environment.apiUrl}/accounts/me/confirm-email/${token}`,
                        { email }
                    )
                    .pipe(
                        map(() => {
                            return true;
                        }),
                        catchError(() => of(false))
                    );
            }

            changeActiveStatusAsManager(id: number, active: boolean) {
                return this.http
                    .put<ActiveStatusDto>(
                        `${environment.apiUrl}/accounts/manager/change-active-status`,
                        {
                            id,
                            active
                        }
                    )
                    .pipe(
                        map(() => {
                            return true;
                        }),
                        catchError(() => of(false))
                    );
            }

            changeActiveStatusAsAdmin(id: number, active: boolean) {
                return this.http
                    .put<ActiveStatusDto>(
                        `${environment.apiUrl}/accounts/admin/change-active-status`,
                        {
                            id,
                            active
                        }
                    )
                    .pipe(
                        map(() => {
                            return true;
                        }),
                        catchError(() => of(false))
                    );
            }
        }
    }
}
