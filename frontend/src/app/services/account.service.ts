import { Injectable } from '@angular/core';
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
            .post(`${environment.apiUrl}/accounts/me/change-email`, null)
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
