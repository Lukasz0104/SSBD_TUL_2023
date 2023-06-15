import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { AuthService } from '../../shared/services/auth.service';
import { PlaceReportYear } from '../model/place-report-year';
import { PlaceReportMonth } from '../model/place-report-month';
import { catchError, EMPTY, Observable } from 'rxjs';
import { ToastService } from '../../shared/services/toast.service';
import {
    BuildingReport,
    BuildingReportYearAndMonths
} from '../model/building-report';

@Injectable({
    providedIn: 'root'
})
export class ReportService {
    private readonly reportUrl = `${this.config.apiUrl}/reports`;

    constructor(
        private http: HttpClient,
        private config: AppConfigService,
        private authService: AuthService,
        private toastService: ToastService
    ) {}

    getReportForPlaceAndYear(id: number, year: number) {
        return this.http
            .get<PlaceReportYear>(
                `${this.transformUrl()}/place/${id}/report/year?year=${year}`
            )
            .pipe(
                catchError((err: HttpErrorResponse) => {
                    this.toastService.handleError(
                        'toast.report.get-place-report-fail',
                        'get-report-place',
                        err
                    );
                    return EMPTY;
                })
            );
    }

    getReportForPlaceAndYearAndMonth(
        id: number,
        year: number,
        month: number,
        full: boolean
    ) {
        return this.http
            .get<PlaceReportMonth>(
                `${this.transformUrl()}/place/${id}/report/month?year=${year}&month=${month}&full=${full}`
            )
            .pipe(
                catchError((err: HttpErrorResponse) => {
                    this.toastService.handleError(
                        'toast.report.get-place-report-fail',
                        'get-report-place',
                        err
                    );
                    return EMPTY;
                })
            );
    }

    isReportForPlace(id: number, year: number) {
        return this.http
            .get<boolean>(
                `${this.transformUrl()}/place/${id}/is-report?year=${year}`
            )
            .pipe(
                catchError((err: HttpErrorResponse) => {
                    this.toastService.handleError(
                        'toast.report.is-report-for-place-fail',
                        'is-report-for-place',
                        err
                    );
                    return EMPTY;
                })
            );
    }

    private transformUrl() {
        if (this.authService.isOwner()) {
            return this.reportUrl + '/me';
        }
        return this.reportUrl;
    }

    getYears(): Observable<Map<string, number[]> | null> {
        return this.http.get<Map<string, number[]> | null>(
            `${this.reportUrl}/community`
        );
    }

    getYearsAndMonths(
        buildingId: number
    ): Observable<BuildingReportYearAndMonths[] | null> {
        return this.http.get<BuildingReportYearAndMonths[]>(
            `${this.reportUrl}/buildings/${buildingId}`
        );
    }

    getBuildingReportByYear(
        buildingId: number,
        year: number
    ): Observable<BuildingReport | null> {
        return this.http.get<BuildingReport>(
            `${this.reportUrl}/buildings/${buildingId}/${year}`
        );
    }

    getBuildingReportByYearAndMonth(
        buildingId: number,
        year: number,
        month: number
    ): Observable<BuildingReport | null> {
        return this.http.get<BuildingReport>(
            `${this.reportUrl}/buildings/${buildingId}/${year}/${month}`
        );
    }
}
