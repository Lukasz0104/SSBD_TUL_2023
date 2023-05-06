import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AccessLevel } from '../../../model/access-level';

@Component({
    selector: 'app-choose-access-level',
    templateUrl: './choose-access-level.component.html'
})
export class ChooseAccessLevelComponent {
    @Input() public groups: AccessLevel[] | undefined;

    constructor(public activeModal: NgbActiveModal) {}

    onClick(group: AccessLevel) {
        this.activeModal.close(group);
    }
}
