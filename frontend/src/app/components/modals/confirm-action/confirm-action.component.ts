import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-confirm-action',
    templateUrl: './confirm-action.component.html'
})
export class ConfirmActionComponent {
    @Input() public message: string | undefined;

    constructor(public activeModal: NgbActiveModal) {}

    onClick() {
        this.activeModal.close(true);
    }
}
