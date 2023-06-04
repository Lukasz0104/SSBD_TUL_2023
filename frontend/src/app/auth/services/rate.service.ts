import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { PublicRate } from '../../shared/model/rate';
import { ToastService } from '../../shared/services/toast.service';
import { catchError, map, of, tap } from 'rxjs';
import { ResponseMessage } from '../../shared/model/response-message.enum';

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
                    this.handleError('toast.rate.fail', 'toast.rate', err);
                    switch (err.error.message) {
                        case ResponseMessage.RATE_ALREADY_EFFECTIVE:
                            return of(false);
                    }
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
