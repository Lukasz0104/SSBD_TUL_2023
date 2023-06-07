import { Component, Input, OnInit } from '@angular/core';
import { ReportService } from '../../services/report.service';
import { ToastService } from '../../../shared/services/toast.service';
import { PlaceReportYear } from '../../model/place-report-year';
import { Observable, tap } from 'rxjs';
import { PlaceReportMonth } from '../../model/place-report-month';
import { PlaceService } from '../../services/place.service';
import { ForecastService } from '../../services/forecast.service';
import { AccountingRule } from '../../../shared/model/accounting-rule';

@Component({
    selector: 'app-place-reports',
    templateUrl: './place-reports.component.html'
})
export class PlaceReportsComponent implements OnInit {
    @Input() id: number | undefined;
    years: number[] = [];
    months = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
    currentYear: number | undefined;
    currentMonth = new Date().getMonth();
    validCurrentMonth = true;
    loading = false;
    placeReportYear: Observable<PlaceReportYear> | undefined;
    placeReportMonth: Observable<PlaceReportMonth> | undefined;

    constructor(
        private reportService: ReportService,
        private toastService: ToastService,
        protected placeService: PlaceService,
        private forecastService: ForecastService
    ) {}

    ngOnInit(): void {
        this.initData();
    }

    initData() {
        this.getYears()?.subscribe((y) => {
            this.years = y;
            this.currentYear = this.currentYear ?? y.at(-1);
            this.getPlaceReports();
        });
    }

    getPlaceReports() {
        this.getMonths();
        if (this.currentMonth == 0) {
            this.getPlaceYearReports();
        } else {
            this.getPlaceMonthReports();
        }
    }

    getPlaceYearReports() {
        if (this.id === undefined) {
            this.loading = true;
            this.toastService.showDanger('toast.report.not-found');
        } else {
            if (this.currentYear) {
                this.placeReportYear =
                    this.reportService.getReportForPlaceAndYear(
                        this.id,
                        this.currentYear
                    );
            }
        }
    }

    getPlaceMonthReports() {
        if (this.id === undefined) {
            this.loading = true;
            this.toastService.showDanger('toast.report.not-found');
        } else {
            if (this.currentYear) {
                this.placeReportMonth = this.reportService
                    .getReportForPlaceAndYearAndMonth(
                        this.id,
                        this.currentYear,
                        this.currentMonth
                    )
                    .pipe(
                        tap((res) => {
                            this.validCurrentMonth = res.completeMonth;
                        })
                    );
            }
        }
    }

    getYears() {
        if (this.id) {
            return this.forecastService.getForecastYearsByPlaceId(this.id);
        }
        return null;
    }

    getMonths() {
        if (this.currentYear && this.id) {
            this.reportService
                .isReportForPlace(this.id, this.currentYear)
                .subscribe((res) => {
                    if (res) {
                        if (this.months.indexOf(0) === -1) {
                            this.months.push(0);
                        }
                    } else {
                        if (this.months.indexOf(0) > -1) {
                            this.months.splice(this.months.indexOf(0), 1);
                        }
                        if (this.currentMonth === 0) {
                            this.currentMonth = new Date().getMonth();
                        }
                    }
                });
        }
    }

    changeCurrentYear(year: number) {
        this.currentYear = year;
        this.getPlaceReports();
    }

    changeCurrentMonth(month: number) {
        this.currentMonth = month;
        this.getPlaceReports();
    }

    onReload() {
        this.initData();
    }

    getNumAbs(diff: number) {
        return Math.abs(diff);
    }

    public get RULE(): typeof AccountingRule {
        return AccountingRule;
    }
}
