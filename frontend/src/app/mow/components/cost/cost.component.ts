import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Cost } from '../../model/cost';
import { ActivatedRoute } from '@angular/router';
import { CostsService } from '../../services/costs.service';

@Component({
    selector: 'app-cost',
    templateUrl: './cost.component.html'
})
export class CostComponent implements OnInit {
    id: number | undefined;
    cost$: Observable<Cost | null> | undefined;

    constructor(
        private costsService: CostsService,
        private route: ActivatedRoute
    ) {}

    ngOnInit(): void {
        this.route.queryParams.subscribe((params) => {
            this.id = params['id'];
            if (this.id !== undefined) {
                this.cost$ = this.costsService.getCost(this.id);
            }
        });
    }
}
