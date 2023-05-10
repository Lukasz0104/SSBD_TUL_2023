import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { changeEmailForm } from '../model/email-form';
import { catchError, map, of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ChangeEmailService {
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
            .put<changeEmailForm>(
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
}
