import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppConfigService } from '../../shared/services/app-config.service';
import {
    BuildingReport,
    BuildingReportYearAndMonths
} from '../model/building-report';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ReportService {
    private readonly BUILDING_URL = `${this.config.apiUrl}/buildings`;

    constructor(private http: HttpClient, private config: AppConfigService) {}

    getYearsAndMonths(
        buildingId: number
    ): Observable<BuildingReportYearAndMonths[] | null> {
        return this.http.get<BuildingReportYearAndMonths[]>(
            `${this.BUILDING_URL}/${buildingId}/reports`
        );
    }

    getBuildingReportByYear(
        buildingId: number,
        year: number
    ): Observable<BuildingReport | null> {
        return this.http.get<BuildingReport>(
            `${this.BUILDING_URL}/${buildingId}/reports/${year}`
        );
    }

    getBuildingReportByYearAndMonth(
        buildingId: number,
        year: number,
        month: number
    ): Observable<BuildingReport | null> {
        return this.http.get<BuildingReport>(
            `${this.BUILDING_URL}/${buildingId}/reports/${year}/${month}`
        );
    }
}
