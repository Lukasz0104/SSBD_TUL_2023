import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { AuthService } from '../../shared/services/auth.service';

@Injectable({
    providedIn: 'root'
})
export class ForecastService {
    private readonly forecastUrl = `${this.config.apiUrl}/forecasts`;

    constructor(
        private http: HttpClient,
        private config: AppConfigService,
        private authService: AuthService
    ) {}

    getForecastYearsByPlaceId(id: number) {
        return this.http.get<number[]>(
            `${this.transformUrl()}/years/${id}/place`
        );
    }

    private transformUrl() {
        if (this.authService.isOwner()) {
            return this.forecastUrl + '/me';
        }
        return this.forecastUrl;
    }
}
