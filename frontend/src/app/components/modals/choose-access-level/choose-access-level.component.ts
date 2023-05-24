import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AccessLevels, AccessType } from '../../../model/access-type';

@Component({
    selector: 'app-choose-access-level',
    templateUrl: './choose-access-level.component.html'
})
export class ChooseAccessLevelComponent {
    @Input() public groups: AccessType[] | undefined;

    constructor(public activeModal: NgbActiveModal) {}

    onClick(group: AccessType) {
        this.activeModal.close(group);
    }

    protected readonly AccessLevels = AccessLevels;
}
