import { Component, OnInit } from '@angular/core';
import { ReportService } from '../../services/report.service';

@Component({
    selector: 'app-community-reports',
    templateUrl: './community-reports.component.html'
})
export class CommunityReportsComponent implements OnInit {
    yearsAndMonths: Map<string, number[]> = new Map<string, number[]>();
    years: number[] = [];
    currentYear: number | undefined;
    currentMonth = new Date().getMonth();

    constructor(private reportService: ReportService) {}

    ngOnInit(): void {
        this.init();
    }

    init() {
        this.getYears()?.subscribe((y) => {
            if (y != null) {
                const yearsAndMonths = new Map(Object.entries(y));
                this.yearsAndMonths = yearsAndMonths;
                this.years = Array.from(yearsAndMonths.keys()).map((x) => +x);
                this.currentYear =
                    this.currentYear ?? +this.years[this.years.length - 1];
                for (const year of this.years) {
                    this.yearsAndMonths.get(year.toString())?.push(0);
                }
            }
        });
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
    }

    changeCurrentMonth(month: number) {
        this.currentMonth = month;
    }

    onReload() {
        this.init();
    }
}
