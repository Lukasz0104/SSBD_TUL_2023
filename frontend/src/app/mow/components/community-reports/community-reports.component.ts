import { Component, OnInit } from '@angular/core';
import { ReportService } from '../../services/report.service';
import { BehaviorSubject } from 'rxjs';
import { CommunityReport, ReportEntry } from '../../model/building-report';
import { PlaceService } from '../../services/place.service';
import { AccountingRule } from '../../model/accounting-rule';

@Component({
    selector: 'app-community-reports',
    templateUrl: './community-reports.component.html'
})
export class CommunityReportsComponent implements OnInit {
    yearsAndMonths: Map<string, number[]> = new Map<string, number[]>();
    years: number[] = [];
    currentYear;
    currentMonth;

    protected report$ = new BehaviorSubject<CommunityReport | null>(null);

    constructor(
        private reportService: ReportService,
        protected placeService: PlaceService
    ) {
        const date = new Date();
        this.currentYear = date.getFullYear();
        this.currentMonth = date.getMonth();
    }

    ngOnInit(): void {
        this.init();
    }

    init() {
        this.getYears()?.subscribe((y) => {
            if (y != null) {
                const yearsAndMonths = new Map(Object.entries(y));
                this.yearsAndMonths = yearsAndMonths;
                this.years = Array.from(yearsAndMonths.keys()).map((x) => +x);
                this.currentYear ??= +this.years[this.years.length - 1];
                for (const year of this.years) {
                    this.yearsAndMonths.get(year.toString())?.push(0);
                }
                this.loadReport();
            }
        });
    }

    loadReport() {
        if (this.currentMonth === 0)
            this.reportService
                .getCommunityReportForYear(this.currentYear)
                .subscribe((report) => this.report$.next(report));
        else {
            this.reportService
                .getCommunityReportForYearAndMonth(
                    this.currentYear,
                    this.currentMonth
                )
                .subscribe((report) => this.report$.next(report));
        }
    }

    getYears() {
        return this.reportService.getYears();
    }

    getMonths() {
        if (this.currentYear) {
            return this.yearsAndMonths.get(this.currentYear.toString());
        }
        return [];
    }

    changeCurrentYear(year: number) {
        this.currentYear = year;
        const months = this.yearsAndMonths.get(this.currentYear.toString());
        if (!months?.includes(this.currentMonth) && months) {
            this.currentMonth = months[0];
        }
        this.loadReport();
    }

    changeCurrentMonth(month: number) {
        this.currentMonth = month;
        this.loadReport();
    }

    onReload() {
        this.init();
    }

    protected AccountingRule = AccountingRule;

    protected realValueSum(report: ReportEntry[]) {
        return report
            .map((re) => re.realValue)
            .reduce((total, value) => total + value, 0);
    }

    protected predValueSum(report: ReportEntry[]) {
        return report
            .map((re) => re.predValue)
            .reduce((total, value) => total + value, 0);
    }
}
