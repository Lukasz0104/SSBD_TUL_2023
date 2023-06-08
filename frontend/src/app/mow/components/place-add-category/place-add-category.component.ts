import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { PlaceCategory } from '../../model/place-category';
import { Observable } from 'rxjs';
import { PlaceService } from '../../services/place.service';
import { AccountingRule } from '../../../shared/model/accounting-rule';

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
        protected activeModal: NgbActiveModal
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
}
