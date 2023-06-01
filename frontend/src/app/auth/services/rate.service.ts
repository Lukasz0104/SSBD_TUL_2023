import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { PublicRate } from '../../shared/model/rate';

@Injectable({
    providedIn: 'root'
})
export class RateService {
    constructor(
        private http: HttpClient,
        private appConfig: AppConfigService
    ) {}

    private readonly ratesUrl = `${this.appConfig.apiUrl}/rates`;

    getCurrentRates() {
        return this.http.get<PublicRate[]>(`${this.ratesUrl}`);
    }
}
