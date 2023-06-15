import { Component, OnInit } from '@angular/core';
import { CostsService } from '../../services/costs.service';
import { AuthService } from '../../../shared/services/auth.service';
import { EMPTY, Observable } from 'rxjs';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CostComponent } from '../cost/cost.component';
import { Page } from '../../../shared/model/page';
import { Cost } from '../../model/cost';
import { AddCostComponent } from '../add-cost/add-cost.component';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';

@Component({
    selector: 'app-costs',
    templateUrl: './costs.component.html'
})
export class CostsComponent implements OnInit {
    private _costs$: Observable<Page<Cost>> | undefined;
    years: string[] | undefined;
    categoryNames: string[] | undefined;
    page = 1;
    pageSize = 10;
    sortDirection = 1;
    categoryName: string | undefined;
    year: string | undefined;

    constructor(
        private costsService: CostsService,
        protected authService: AuthService,
        private modalService: NgbModal
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

    openCostDetails(id: number) {
        const modalRef: NgbModalRef = this.modalService.open(CostComponent, {
            centered: true,
            scrollable: true
        });
        modalRef.componentInstance.setCostById(id);
        modalRef.result
            .then((res): void => {
                id = res;
            })
            .catch(() => EMPTY);
        this.getCosts();
    }

    addCost() {
        const modalRef: NgbModalRef = this.modalService.open(AddCostComponent, {
            centered: true,
            scrollable: true
        });
        modalRef.result.then().catch(() => EMPTY);
    }

    removeCost(id: number) {
        const modalRef = this.modalService.open(ConfirmActionComponent, {
            centered: true
        });
        const instance = modalRef.componentInstance as ConfirmActionComponent;

        instance.message = 'modal.confirm-action.remove-cost';
        modalRef.closed.subscribe((res: boolean) => {
            if (res) {
                this.costsService.removeCost(id).subscribe(() => {
                    this.getCosts();
                });
            }
        });
    }

    protected onSortChange() {
        this.sortDirection = -this.sortDirection;
        this.getCosts();
    }

    changeCategoryName(name: string) {
        this.categoryName = name;

        this.getCosts();
    }

    changeYear(year: string) {
        this.year = year;

        this.getCosts();
    }

    get costs$(): Observable<Page<Cost>> | undefined {
        return this._costs$;
    }
}
