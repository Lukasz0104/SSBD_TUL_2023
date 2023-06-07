import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { catchError, map, of, tap } from 'rxjs';
import { ToastService } from '../../shared/services/toast.service';

@Injectable({
    providedIn: 'root'
})
export class ReadingService {
    private readonly BASE_URL = `${this.config.apiUrl}/readings`;

    constructor(
        private http: HttpClient,
        private config: AppConfigService,
        private toastService: ToastService
    ) {}

    addReadingAsOwner(meterId: number, value: number) {
        return this.http
            .post(
                `${this.BASE_URL}/me`,
                { meterId: meterId, value: value },
                {
                    observe: 'response'
                }
            )
            .pipe(
                tap(() => {
                    this.toastService.showSuccess('toast.reading.add-success');
                }),
                map(() => true),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        'toast.add-reading.fail',
                        'toast.reading',
                        err
                    );
                    return of(true);
                })
            );
    }

    addReadingAsManager(meterId: number, value: number, date: string) {
        return this.http
            .post(
                `${this.BASE_URL}`,
                { meterId: meterId, value: value, date: date },
                {
                    observe: 'response'
                }
            )
            .pipe(
                tap(() => {
                    this.toastService.showSuccess('toast.reading.add-success');
                }),
                map(() => true),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        'toast.reading.add-fail',
                        'toast.reading',
                        err
                    );
                    return of(true);
                })
            );
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
