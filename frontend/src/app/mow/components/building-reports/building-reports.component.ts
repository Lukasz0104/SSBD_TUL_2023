import { Component, OnInit } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ToastService } from '../../../shared/services/toast.service';
import { ReportService } from '../../services/report.service';
import {
    BuildingReport,
    BuildingReportYearAndMonths
} from '../../model/building-report';
import { ActivatedRoute } from '@angular/router';
import { AccountingRule } from '../../model/accounting-rule';
import { PlaceService } from '../../services/place.service';

@Component({
    selector: 'app-building-reports',
    templateUrl: './building-reports.component.html'
})
export class BuildingReportsComponent implements OnInit {
    report$: Observable<BuildingReport | null> | undefined;
    yearAndMonth$: Observable<BuildingReportYearAndMonths[] | null> | undefined;
    report: BuildingReport | undefined;
    buildingId: number | undefined;
    year: number | undefined;
    month: number | undefined;
    months: number[] | undefined;

    constructor(
        private reportService: ReportService,
        private route: ActivatedRoute,
        private toastService: ToastService,
        protected placeService: PlaceService
    ) {}

    ngOnInit(): void {
        this.route.queryParams.subscribe((params) => {
            this.buildingId = params['id'];
            if (this.buildingId !== undefined) {
                this.yearAndMonth$ = this.reportService.getYearsAndMonths(
                    this.buildingId
                );
            } else {
                this.toastService.showDanger('toast.report.not-found');
            }
        });
        this.choseYear(new Date().getFullYear() - 1);
    }

    choseYear(year: number) {
        this.year = year;
        this.month = undefined;
        if (this.year && this.buildingId) {
            this.yearAndMonth$
                ?.pipe(
                    map((xs) => {
                        this.months = xs
                            ?.filter((x) => x.year == this.year)
                            .map((x) => x.months)
                            .at(0);
                        this.getYearReport();
                    })
                )
                .subscribe();
        } else {
            this.report$ = undefined;
        }
    }

    choseMonth(month: number) {
        this.month = month;
        if (this.month) this.getMonthReport();
        else this.getYearReport();
    }

    getYearReport() {
        if (this.year && this.buildingId) {
            this.report$ = this.reportService.getBuildingReportByYear(
                this.buildingId,
                this.year
            );
        }
    }

    getMonthReport() {
        if (this.year && this.buildingId && this.month) {
            this.report$ = this.reportService.getBuildingReportByYearAndMonth(
                this.buildingId,
                this.year,
                this.month
            );
        }
    }

    reload() {
        if (this.month) this.getMonthReport();
        else this.getYearReport();
    }

    protected readonly AccountingRule = AccountingRule;
}
