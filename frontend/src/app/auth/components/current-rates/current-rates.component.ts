import { Component } from '@angular/core';
import { AccountingRule } from '../../../shared/model/accounting-rule';
import { Observable } from 'rxjs';
import { RateService } from '../../services/rate.service';
import { PublicRate } from '../../../shared/model/rate';
import { PlaceService } from '../../../mow/services/place.service';

@Component({
    selector: 'app-current-rates',
    templateUrl: './current-rates.component.html'
})
export class CurrentRatesComponent {
    protected readonly AccountingRule = AccountingRule;

    currentRates$: Observable<PublicRate[]> | undefined;

    constructor(
        private rateService: RateService,
        private placeService: PlaceService
    ) {}

    ngOnInit() {
        this.getCurrentRates();
    }

    getCurrentRates() {
        this.currentRates$ = this.rateService.getCurrentRates();
    }

    public get RULE(): typeof AccountingRule {
        return AccountingRule;
    }

    public getIcon(category: string): string {
        return this.placeService.pictureMap.get(category) ?? 'bi-coin';
    }
}
