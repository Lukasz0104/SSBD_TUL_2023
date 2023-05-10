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
}
