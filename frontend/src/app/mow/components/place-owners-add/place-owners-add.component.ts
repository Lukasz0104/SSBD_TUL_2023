import { Component } from '@angular/core';
import { PlaceService } from '../../services/place.service';
import { EMPTY, Observable } from 'rxjs';
import { PlaceOwner } from '../../model/place';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-place-owners-add',
    templateUrl: './place-owners-add.component.html'
})
export class PlaceOwnersAddComponent {
    placeId: number | undefined;
    protected notOwners$: Observable<PlaceOwner[]> = EMPTY;

    constructor(
        private placeService: PlaceService,
        protected activeModal: NgbActiveModal
    ) {}

    setPlace(placeId: number) {
        this.placeId = placeId;
        this.getNotOwners();
    }

    getNotOwners() {
        if (this.placeId) {
            this.notOwners$ = this.placeService.getPlaceNotOwners(this.placeId);
        }
    }

    addOwner(ownerDataId: number) {
        if (this.placeId) {
            this.placeService
                .addOwner(ownerDataId, this.placeId)
                .subscribe(() => this.getNotOwners());
        }
    }
}
