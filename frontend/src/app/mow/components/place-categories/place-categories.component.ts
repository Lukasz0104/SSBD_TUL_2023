import { Component, Input, OnInit } from '@angular/core';
import { PlaceCategory } from '../../model/place-category';
import { PlaceService } from '../../services/place.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { AccountingRule } from '../../model/accounting-rule';
import { PlaceAddCategoryComponent } from '../place-add-category/place-add-category.component';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';
import { ForecastService } from '../../services/forecast.service';
import { AddInitialReadingComponent } from '../add-initial-reading/add-initial-reading.component';

@Component({
    selector: 'app-place-categories',
    templateUrl: './place-categories.component.html',
    styleUrls: ['./place-categories.component.css']
})
export class PlaceCategoriesComponent implements OnInit {
    placeCategories$: Observable<PlaceCategory[]> | undefined;
    @Input() public id: number | undefined;
    @Input() public active: boolean | undefined;
    editing = false;
    chosen = -1;
    accountingRule = AccountingRule.UNIT;

    constructor(
        private placeService: PlaceService,
        private modalService: NgbModal,
        private forecastService: ForecastService
    ) {}

    ngOnInit() {
        this.getPlaceCategories();
    }

    getPlaceCategories() {
        if (this.id != null) {
            this.placeCategories$ = this.placeService.getPlaceCategories(
                this.id
            );
        }
    }

    public get RULE(): typeof AccountingRule {
        return AccountingRule;
    }

    public getIcon(category: string): string {
        return this.placeService.pictureMap.get(category) ?? 'bi-coin';
    }

    setChosen(id: number, rule: AccountingRule) {
        if (this.editing) {
            if (this.chosen === id) {
                this.chosen = -1;
            } else {
                this.chosen = id;
            }
            this.accountingRule = rule;
        }
    }

    removeCategory() {
        const modalRef = this.modalService.open(ConfirmActionComponent, {
            centered: true
        });
        modalRef.componentInstance.message =
            'component.place.categories.delete-confirm';
        modalRef.componentInstance.danger =
            'component.place.categories.delete-confirm-danger';
        modalRef.closed.subscribe((result) => {
            if (result) {
                this.placeService
                    .removeCategory(this.id, this.chosen)
                    .subscribe(() => {
                        this.getPlaceCategories();
                        this.chosen = -1;
                        this.accountingRule = AccountingRule.UNIT;
                    });
            }
        });
    }

    stopEdit() {
        this.editing = false;
        this.chosen = -1;
        this.accountingRule = AccountingRule.UNIT;
    }

    addCategory() {
        const modalRef: NgbModalRef = this.modalService.open(
            PlaceAddCategoryComponent,
            { centered: true, size: 'lg', backdrop: 'static' }
        );
        modalRef.componentInstance.placeId = this.id;
        modalRef.closed.subscribe(() => {
            this.getPlaceCategories();
        });
    }

    addOverdueForecast() {
        const modalRef: NgbModalRef = this.modalService.open(
            AddInitialReadingComponent,
            { centered: true }
        );
        modalRef.componentInstance.value = false;
        modalRef.componentInstance.accountingRule = this.accountingRule;
        modalRef.closed.subscribe((result) => {
            if (result > 0 && this.id) {
                this.forecastService
                    .addCurrentForecast(this.id, this.chosen, result)
                    .subscribe(() => {
                        this.getPlaceCategories();
                        this.chosen = -1;
                        this.accountingRule = AccountingRule.UNIT;
                    });
            }
        });
    }

    onReload() {
        this.getPlaceCategories();
    }
}
