import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, map, Observable, of } from 'rxjs';
import { ToastService } from './toast.service';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    constructor(private http: HttpClient, private toastService: ToastService) {}

    changePassword(dto: object): Observable<boolean> {
        return this.http
            .put(`${environment.apiUrl}/accounts/me/change-password`, dto)
            .pipe(
                map(() => {
                    this.toastService.showSuccess(
                        'toast.change-password-success'
                    );
                    return true;
                }),
                catchError((res: HttpErrorResponse) => {
                    this.toastService.showDanger(`toast.${res.error.message}`);
                    return of(false);
                })
            );
    }
}
