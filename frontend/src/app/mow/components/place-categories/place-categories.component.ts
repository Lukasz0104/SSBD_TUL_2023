import { Component, Input, OnInit } from '@angular/core';
import { PlaceCategory } from '../../model/place-category';
import { PlaceService } from '../../services/place.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { AccountingRule } from '../../model/accounting-rule';
import { PlaceAddCategoryComponent } from '../place-add-category/place-add-category.component';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';

@Component({
    selector: 'app-place-categories',
    templateUrl: './place-categories.component.html',
    styleUrls: ['./place-categories.component.css']
})
export class PlaceCategoriesComponent implements OnInit {
    placeCategories$: Observable<PlaceCategory[]> | undefined;
    @Input() public id: number | undefined;
    editing = false;
    chosen = -1;

    constructor(
        private placeService: PlaceService,
        private modalService: NgbModal
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

    setChosen(id: number) {
        if (this.editing) {
            if (this.chosen === id) {
                this.chosen = -1;
            } else {
                this.chosen = id;
            }
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
                    });
            }
        });
    }

    stopEdit() {
        this.editing = false;
        this.chosen = -1;
    }

    addCategory() {
        const modalRef: NgbModalRef = this.modalService.open(
            PlaceAddCategoryComponent,
            { centered: true, size: 'lg' }
        );
        modalRef.componentInstance.placeId = this.id;
        modalRef.closed.subscribe(() => {
            this.getPlaceCategories();
        });
    }

    onReload() {
        this.getPlaceCategories();
    }
}
