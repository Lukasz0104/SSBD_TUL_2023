import { Component, Input, OnInit } from '@angular/core';
import {
    NgbActiveModal,
    NgbModal,
    NgbModalRef
} from '@ng-bootstrap/ng-bootstrap';
import { PlaceCategory } from '../../model/place-category';
import { Observable } from 'rxjs';
import { PlaceService } from '../../services/place.service';
import { AccountingRule } from '../../model/accounting-rule';
import { AddInitialReadingComponent } from './add-initial-reading/add-initial-reading.component';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';

@Component({
    selector: 'app-place-add-category',
    templateUrl: './place-add-category.component.html',
    styleUrls: ['./place-add-category.component.css']
})
export class PlaceAddCategoryComponent implements OnInit {
    missingCategories$: Observable<PlaceCategory[]> | undefined;
    @Input() placeId: number | undefined;

    constructor(
        private placeService: PlaceService,
        protected activeModal: NgbActiveModal,
        private modalService: NgbModal
    ) {}

    ngOnInit(): void {
        this.getMissingCategories();
    }

    public getIcon(category: string): string {
        return this.placeService.pictureMap.get(category) ?? 'bi-coin';
    }

    public get RULE(): typeof AccountingRule {
        return AccountingRule;
    }

    getMissingCategories() {
        if (this.placeId) {
            this.missingCategories$ =
                this.placeService.getPlaceMissingCategories(this.placeId);
        }
    }

    addCategory(category: PlaceCategory) {
        if (this.placeId) {
            this.placeService
                .checkIfReadingRequired(this.placeId, category.categoryId)
                .subscribe((result) => {
                    if (result) {
                        const modalRef: NgbModalRef = this.modalService.open(
                            AddInitialReadingComponent,
                            { centered: true }
                        );
                        modalRef.closed.subscribe((result) => {
                            if (result > 0) {
                                this.confirm(
                                    this.placeId,
                                    category.categoryId,
                                    result
                                );
                            }
                        });
                    } else {
                        this.confirm(this.placeId, category.categoryId, null);
                    }
                });
        }
    }

    confirm(
        placeId: number | undefined,
        categoryId: number,
        newReading: number | null
    ) {
        const modalRef = this.modalService.open(ConfirmActionComponent, {
            centered: true
        });
        const instance = modalRef.componentInstance as ConfirmActionComponent;

        instance.message = 'component.place.categories.confirm';
        instance.danger = 'component.place.categories.confirm-danger';
        modalRef.closed.subscribe((res: boolean) => {
            if (res) {
                if (this.placeId) {
                    this.placeService
                        .addCategory(this.placeId, categoryId, newReading)
                        .subscribe((res) => {
                            if (res) {
                                this.activeModal.close();
                            } else {
                                this.getMissingCategories();
                            }
                        });
                }
            }
        });
    }

    onReload() {
        this.getMissingCategories();
    }
}
