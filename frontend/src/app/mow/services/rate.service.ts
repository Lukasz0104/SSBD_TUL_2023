import { Injectable } from '@angular/core';
import { AppConfigService } from '../../shared/services/app-config.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, map, of, tap } from 'rxjs';
import { ResponseMessage } from '../../shared/model/response-message.enum';
import { ToastService } from '../../shared/services/toast.service';
import { AddRate } from '../model/rate';

@Injectable({
    providedIn: 'root'
})
export class RateService {
    private readonly ratesUrl = `${this.appConfig.apiUrl}/rates`;

    constructor(
        private appConfig: AppConfigService,
        private http: HttpClient,
        private toastService: ToastService
    ) {}

    addRate(dto: AddRate) {
        return this.http
            .post<AddRate>(`${this.ratesUrl}`, dto, {
                observe: 'response'
            })
            .pipe(
                tap(() => {
                    this.toastService.showSuccess('toast.rate.add-success');
                }),
                map(() => true),
                catchError((err: HttpErrorResponse) => {
                    this.handleError('toast.rate.fail', 'toast.rate', err);
                    switch (err.error.message) {
                        case ResponseMessage.CATEGORY_NOT_FOUND:
                            return of(true);
                        case ResponseMessage.RATE_NOT_UNIQUE:
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
