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
    months: number[] = [];
    currentYear: number | undefined;
    currentMonth = new Date().getMonth() + 1;
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
            this.currentYear = this.currentYear ?? y[y.length - 1];
            this.getData();
        });
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

    getPlaceMonthReports(full: boolean) {
        if (this.id === undefined) {
            this.loading = true;
            this.toastService.showDanger('toast.report.not-found');
        } else {
            if (this.currentYear) {
                let month = this.currentMonth;
                if (full) {
                    month = new Date().getMonth();
                }
                this.placeReportMonth = this.reportService
                    .getReportForPlaceAndYearAndMonth(
                        this.id,
                        this.currentYear,
                        month,
                        full
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

    getData() {
        this.months = [];
        if (this.currentYear && this.id) {
            const id = this.id;
            const currentYear = this.currentYear;
            this.reportService
                .isReportForPlace(id, currentYear)
                .subscribe((res) => {
                    this.forecastService
                        .getMinMonthForPlaceAndYear(id, currentYear)
                        .subscribe((result) => {
                            for (let i = result; i < 13; i++) {
                                this.months.push(i);
                            }
                            if (res) {
                                if (this.months.indexOf(0) === -1) {
                                    this.months.push(0);
                                }
                                if (this.months.indexOf(-1) > -1) {
                                    this.months.splice(
                                        this.months.indexOf(-1),
                                        1
                                    );
                                }
                            } else {
                                if (this.months.indexOf(-1) === -1) {
                                    this.months.push(-1);
                                }
                                if (this.months.indexOf(0) > -1) {
                                    this.months.splice(
                                        this.months.indexOf(0),
                                        1
                                    );
                                }
                                if (this.currentMonth === 0) {
                                    this.currentMonth = -1;
                                }
                            }
                            if (!this.months.includes(this.currentMonth)) {
                                this.currentMonth = Math.min.apply(
                                    null,
                                    this.months
                                );
                            }
                            if (this.currentMonth == 0) {
                                this.getPlaceYearReports();
                            } else if (this.currentMonth == -1) {
                                this.getPlaceMonthReports(true);
                            } else {
                                this.getPlaceMonthReports(false);
                            }
                        });
                });
        }
    }

    changeCurrentYear(year: number) {
        this.currentYear = year;
        this.getData();
    }

    changeCurrentMonth(month: number) {
        this.currentMonth = month;
        this.getData();
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
