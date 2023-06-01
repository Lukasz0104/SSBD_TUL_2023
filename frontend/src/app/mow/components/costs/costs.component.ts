import { Component, OnInit } from '@angular/core';
import { CostPage } from '../../model/cost-page';
import { CostsService } from '../../services/costs.service';
import { AuthService } from '../../../shared/services/auth.service';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-costs',
    templateUrl: './costs.component.html'
})
export class CostsComponent implements OnInit {
    private _costs$: Observable<CostPage> | undefined;
    toggledYear = false;
    toggledName = false;
    years: string[] | undefined;
    categoryNames: string[] | undefined;
    page = 1;
    pageSize = 10;
    sortDirection = 1;
    categoryName: string | undefined;
    year: string | undefined;

    monthMap = new Map([
        ['JANUARY', 'component.costs.months.january'],
        ['FEBRUARY', 'component.costs.months.february'],
        ['MARCH', 'component.costs.months.march'],
        ['APRIL', 'component.costs.months.april'],
        ['MAY', 'component.costs.months.may'],
        ['JUNE', 'component.costs.months.june'],
        ['JULY', 'component.costs.months.july'],
        ['AUGUST', 'component.costs.months.august'],
        ['SEPTEMBER', 'component.costs.months.september'],
        ['OCTOBER', 'component.costs.months.october'],
        ['NOVEMBER', 'component.costs.months.november'],
        ['DECEMBER', 'component.costs.months.december']
    ]);

    constructor(
        private costsService: CostsService,
        protected authService: AuthService
    ) {}

    ngOnInit() {
        this.costsService.getYearsFromCosts().subscribe((years) => {
            this.years = years;
        });
        this.costsService.getCategoryNamesFromCosts().subscribe((names) => {
            this.categoryNames = names;
        });
    }
    getCosts() {
        if (this.year === undefined || this.categoryName === undefined) {
            this._costs$ = undefined;
            return;
        }
        this._costs$ = this.costsService.getAllCosts(
            this.page - 1,
            this.pageSize,
            this.sortDirection === 1,
            this.year,
            this.categoryName
        );
    }
    toggleYear() {
        if (!this.toggledYear) {
            this.toggledYear = true;
        }
    }
    toggleName() {
        if (!this.toggledName) {
            this.toggledName = true;
        }
    }

    resetToggleName() {
        this.toggledName = false;
    }

    resetToggleYear() {
        this.toggledYear = false;
    }
    reload() {
        if (this.toggledName && this.toggledYear) {
            this.getCosts();
        }
    }

    protected onSortChange() {
        this.sortDirection = -this.sortDirection;
        this.reload();
    }

    changeCategoryName(name: string) {
        this.categoryName = name;
        if (this.toggledYear) {
            this.getCosts();
        }
    }

    changeYear(year: string) {
        this.year = year;
        if (this.toggledName) {
            this.getCosts();
        }
    }

    get costs$(): Observable<CostPage> | undefined {
        return this._costs$;
    }
}
