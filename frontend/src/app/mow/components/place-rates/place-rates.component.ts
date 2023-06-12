import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { OwnPlaceCategory } from '../../model/place-category';
import { PlaceService } from '../../services/place.service';
import { AccountingRule } from '../../model/accounting-rule';
import { DatePipe } from '@angular/common';

@Component({
    selector: 'app-place-rates',
    templateUrl: './place-rates.component.html'
})
export class PlaceRatesComponent implements OnInit {
    ownPlaceCategories$: Observable<OwnPlaceCategory[]> | undefined;
    @Input() public id: number | undefined;

    constructor(
        protected placeService: PlaceService,
        private datePipe: DatePipe
    ) {}

    ngOnInit() {
        this.getOwnPlaceCategories();
    }

    transformDate(date: Date) {
        if (date == null) {
            return '-';
        }
        return `${this.datePipe.transform(
            date.toLocaleString(),
            'dd/MM/yy HH:mm:ss'
        )}`;
    }

    getOwnPlaceCategories() {
        if (this.id != null) {
            this.ownPlaceCategories$ = this.placeService.getOwnPlaceCategories(
                this.id
            );
        }
    }

    onReload() {
        this.getOwnPlaceCategories();
    }

    public get RULE(): typeof AccountingRule {
        return AccountingRule;
    }
}
