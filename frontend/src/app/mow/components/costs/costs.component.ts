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
    months: string[] = [
        'months.january',
        'months.february',
        'months.march',
        'months.april',
        'months.may',
        'months.june',
        'months.july',
        'months.august',
        'months.september',
        'months.october',
        'months.november',
        'months.december'
    ]; //FIXME TEMPORARY
    page = 1;
    pageSize = 10;
    sortDirection = 1;
    categoryName: string | undefined;
    year: string | undefined;

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
        console.log(this.toggledYear);
        if (this.toggledYear) {
            this.getCosts();
        }
    }

    changeYear(year: string) {
        this.year = year;
        console.log(this.toggledName);
        if (this.toggledName) {
            this.getCosts();
        }
    }

    get costs$(): Observable<CostPage> | undefined {
        return this._costs$;
    }
}
