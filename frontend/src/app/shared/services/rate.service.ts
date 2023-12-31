import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AppConfigService } from './app-config.service';
import { PublicRate } from '../../mow/model/rate';
import { ToastService } from './toast.service';
import { catchError, map, of, tap } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class RateService {
    constructor(
        private http: HttpClient,
        private appConfig: AppConfigService,
        private toastService: ToastService
    ) {}

    private readonly ratesUrl = `${this.appConfig.apiUrl}/rates`;

    getCurrentRates() {
        return this.http.get<PublicRate[]>(`${this.ratesUrl}`);
    }

    removeRate(id: number) {
        return this.http
            .delete(`${this.ratesUrl}/${id}`, {
                observe: 'response'
            })
            .pipe(
                tap(() => {
                    this.toastService.showSuccess('toast.rate.remove-success');
                }),
                map(() => true),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        'toast.rate.remove-fail',
                        'toast.rate',
                        err
                    );
                    return of(false);
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
