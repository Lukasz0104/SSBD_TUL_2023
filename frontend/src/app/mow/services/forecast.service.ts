import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { AuthService } from '../../shared/services/auth.service';
import { catchError, EMPTY, map } from 'rxjs';
import { ToastService } from '../../shared/services/toast.service';

@Injectable({
    providedIn: 'root'
})
export class ForecastService {
    private readonly forecastUrl = `${this.config.apiUrl}/forecasts`;

    constructor(
        private http: HttpClient,
        private config: AppConfigService,
        private authService: AuthService,
        private toastService: ToastService
    ) {}

    getForecastYearsByPlaceId(id: number) {
        return this.http
            .get<number[]>(`${this.transformUrl()}/years/${id}/place`)
            .pipe(
                catchError((err: HttpErrorResponse) => {
                    this.toastService.handleError(
                        'toast.forecast.get-years-by-place-fail',
                        'get-years-by-place',
                        err
                    );
                    return EMPTY;
                })
            );
    }

    addCurrentForecast(
        placeId: number,
        categoryId: number,
        amount: number | null
    ) {
        return this.http
            .post(`${this.forecastUrl}/add-current`, {
                placeId,
                categoryId,
                amount
            })
            .pipe(
                map(() => {
                    this.toastService.showSuccess(
                        'toast.forecast.add-current-forecast-success'
                    );
                }),
                catchError((err: HttpErrorResponse) => {
                    this.toastService.handleError(
                        'toast.forecast.add-current-forecast-fail',
                        'add-current-forecast',
                        err
                    );
                    return EMPTY;
                })
            );
    }

    getMinMonthForPlaceAndYear(id: number, year: number) {
        return this.http
            .get<number>(
                `${this.transformUrl()}/min-month/${id}/place?year=${year}`
            )
            .pipe(
                catchError((err: HttpErrorResponse) => {
                    this.toastService.handleError(
                        'toast.forecast.get-min-months-fail',
                        'get-min-month',
                        err
                    );
                    return EMPTY;
                })
            );
    }

    private transformUrl() {
        if (this.authService.isOwner()) {
            return this.forecastUrl + '/me';
        }
        return this.forecastUrl;
    }
}
