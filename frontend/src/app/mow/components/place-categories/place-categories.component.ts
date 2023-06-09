import { Component, Input, OnInit } from '@angular/core';
import { PlaceCategory } from '../../model/place-category';
import { PlaceService } from '../../services/place.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';
import { AccountingRule } from '../../../shared/model/accounting-rule';
import { PlaceAddCategoryComponent } from '../place-add-category/place-add-category.component';

@Component({
    selector: 'app-place-categories',
    templateUrl: './place-categories.component.html',
    styleUrls: ['./place-categories.component.css']
})
export class PlaceCategoriesComponent implements OnInit {
    placeCategories$: Observable<PlaceCategory[]> | undefined;
    @Input() public id: number | undefined;
    deleting = false;
    chosen: number[] = [];

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

    addNumbers(id: number) {
        if (this.deleting && !this.chosen.includes(id)) {
            this.chosen.push(id);
        } else {
            this.chosen.splice(this.chosen.indexOf(id), 1);
        }
    }

    onSave() {
        if (this.chosen.length > 0) {
            const modalRef: NgbModalRef = this.modalService.open(
                ConfirmActionComponent,
                { centered: true }
            );
            modalRef.componentInstance.message =
                'component.place.categories.action-confirm';
            modalRef.componentInstance.danger = '';
            modalRef.closed.subscribe((result) => {
                if (result) {
                    this.deleting = false;
                    this.chosen.splice(0);
                }
            });
        } else {
            this.deleting = false;
        }
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
