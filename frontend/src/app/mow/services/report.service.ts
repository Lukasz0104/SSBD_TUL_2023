import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { catchError, map } from 'rxjs';
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

    getReportYearsForPlace(id: number) {
        return this.http
            .get<number[]>(`${this.reportUrl}/place/${id}/years`)
            .pipe(
                map((res) => {
                    return res;
                }),
                catchError(() => {
                    return [];
                })
            );
    }

    getReportForPlaceAndYear(id: number, year: number) {
        let url = this.reportUrl;
        if (this.authService.isOwner()) {
            url = url + '/me';
        }
        return this.http.get<PlaceReportYear>(
            `${url}/place/${id}/report/year?year=${year}`
        );
    }

    getReportForPlaceAndYearAndMonth(id: number, year: number, month: number) {
        let url = this.reportUrl;
        if (this.authService.isOwner()) {
            url = url + '/me';
        }
        return this.http.get<PlaceReportMonth>(
            `${url}/place/${id}/report/month?year=${year}&month=${month}`
        );
    }
}
