import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-confirm-action',
    templateUrl: './confirm-action.component.html'
})
export class ConfirmActionComponent {
    @Input() public message: string;
    @Input() public danger: string;

    constructor(public activeModal: NgbActiveModal) {
        this.message = '';
        this.danger = '';
    }

    onClick() {
        this.activeModal.close(true);
    }
}
