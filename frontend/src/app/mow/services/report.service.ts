import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { AuthService } from '../../shared/services/auth.service';
import { PlaceReportYear } from '../model/place-report-year';
import { PlaceReportMonth } from '../model/place-report-month';

@Injectable({
    providedIn: 'root'
})
export class ReportService {
    private readonly reportUrl = `${this.config.apiUrl}/reports`;

    constructor(
        private http: HttpClient,
        private config: AppConfigService,
        private authService: AuthService
    ) {}

    getReportForPlaceAndYear(id: number, year: number) {
        return this.http.get<PlaceReportYear>(
            `${this.transformUrl()}/place/${id}/report/year?year=${year}`
        );
    }

    getReportForPlaceAndYearAndMonth(id: number, year: number, month: number) {
        return this.http.get<PlaceReportMonth>(
            `${this.transformUrl()}/place/${id}/report/month?year=${year}&month=${month}`
        );
    }

    isReportForPlace(id: number, year: number) {
        return this.http.get<boolean>(
            `${this.transformUrl()}/place/${id}/is-report?year=${year}`
        );
    }

    private transformUrl() {
        if (this.authService.isOwner()) {
            return this.reportUrl + '/me';
        }
        return this.reportUrl;
    }
}
