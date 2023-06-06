import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Cost } from '../../model/cost';
import { CostsService } from '../../services/costs.service';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-cost',
    templateUrl: './cost.component.html'
})
export class CostComponent {
    id: number | undefined;
    cost$: Observable<Cost | null> | undefined;

    constructor(
        public activeModal: NgbActiveModal,
        private modalService: NgbModal,
        private costsService: CostsService
    ) {}

    setCostById(id: number) {
        if (id !== undefined) {
            this.cost$ = this.costsService.getCost(id);
        }
    }
}
