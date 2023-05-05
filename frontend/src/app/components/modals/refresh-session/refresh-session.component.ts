import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-refresh-session',
    templateUrl: './refresh-session.component.html',
    styleUrls: ['./refresh-session.component.css']
})
export class RefreshSessionComponent {
    constructor(public activeModal: NgbActiveModal) {}

    onClick(refresh: boolean) {
        this.activeModal.close(refresh);
    }
}
