import { Component, OnInit } from '@angular/core';
import { CostPage } from '../../model/cost';
import { CostsService } from '../../services/costs.service';
import { AuthService } from '../../../shared/services/auth.service';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-costs',
    templateUrl: './costs.component.html'
})
export class CostsComponent implements OnInit {
    private _costs$: Observable<CostPage> | undefined;
    years: string[] | undefined;
    categoryNames: string[] | undefined;
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

    reload() {
        this.getCosts();
    }

    protected onSortChange() {
        this.sortDirection = -this.sortDirection;
        this.reload();
    }

    changeCategoryName(name: string) {
        this.categoryName = name;

        this.getCosts();
    }

    changeYear(year: string) {
        this.year = year;

        this.getCosts();
    }

    get costs$(): Observable<CostPage> | undefined {
        return this._costs$;
    }
}
