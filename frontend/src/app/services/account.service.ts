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
import { ChangeEmailForm } from '../model/email-form';
import { ActiveStatusDto } from '../model/active-status-dto';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    ifMatch = '';
    private accountsUrl = `${environment.apiUrl}/accounts`;

    constructor(private http: HttpClient, private toastService: ToastService) {}

    getOwnProfile() {
        return this.http
            .get<OwnAccount>(`${this.accountsUrl}/me`, {
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
            .get<Account>(`${this.accountsUrl}/${id}`, {
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
            .put<OwnAccount>(`${this.accountsUrl}/me`, dto, {
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
                `${this.accountsUrl}/owners/unapproved`
            );
        } else if (type == AccessType.MANAGER) {
            return this.http.get<Account[]>(
                `${this.accountsUrl}/managers/unapproved`
            );
        }
        return of([]);
    }

    changeEmail() {
        return this.http.post(`${this.accountsUrl}/me/change-email`, null).pipe(
            map(() => {
                this.toastService.showSuccess('mail.sent.success'); //fixme
                return true;
            }),
            catchError((err: HttpErrorResponse) => {
                this.toastService.showDanger(err.error.message);
                return of(false);
            })
        );
    }

    confirmEmail(email: string, token: string) {
        return this.http
            .put<ChangeEmailForm>(
                `${this.accountsUrl}/me/confirm-email/${token}`,
                { email }
            )
            .pipe(
                map(() => {
                    this.toastService.showSuccess('email.change.success');
                    return true;
                }),
                catchError((err: HttpErrorResponse) => {
                    this.toastService.showDanger(err.error.message);
                    return of(false);
                })
            );
    }

    changeActiveStatusAsManager(id: number, active: boolean) {
        return this.http
            .put<ActiveStatusDto>(
                `${this.accountsUrl}/manager/change-active-status`,
                {
                    id,
                    active
                }
            )
            .pipe(
                map(() => {
                    this.toastService.showSuccess('status.change.success'); //todo
                    return true;
                }),
                catchError((err: HttpErrorResponse) => {
                    this.toastService.showDanger(err.error.message);
                    return of(false);
                })
            );
    }

    changeActiveStatusAsAdmin(id: number, active: boolean) {
        return this.http
            .put<ActiveStatusDto>(
                `${this.accountsUrl}/admin/change-active-status`,
                {
                    id,
                    active
                }
            )
            .pipe(
                map(() => {
                    this.toastService.showSuccess('status.change.success'); //todo
                    return true;
                }),
                catchError((err: HttpErrorResponse) => {
                    this.toastService.showDanger(err.error.message);
                    return of(false);
                })
            );
    }
}
