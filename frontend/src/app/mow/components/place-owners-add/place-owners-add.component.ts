import { Component } from '@angular/core';
import { PlaceService } from '../../services/place.service';
import { EMPTY, Observable } from 'rxjs';
import { PlaceOwner } from '../../model/place';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';

@Component({
    selector: 'app-place-owners-add',
    templateUrl: './place-owners-add.component.html'
})
export class PlaceOwnersAddComponent {
    placeId: number | undefined;
    protected notOwners$: Observable<PlaceOwner[]> = EMPTY;

    constructor(
        private placeService: PlaceService,
        protected activeModal: NgbActiveModal,
        private modalService: NgbModal
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
            const modalRef = this.modalService.open(ConfirmActionComponent);
            const instance =
                modalRef.componentInstance as ConfirmActionComponent;

            instance.message = 'modal.confirm-action.place-owners-add';
            instance.danger = '';
            modalRef.result.then((res: boolean): void => {
                if (res && this.placeId) {
                    this.placeService
                        .addOwner(ownerDataId, this.placeId)
                        .subscribe(() => this.getNotOwners());
                }
            });
        }
    }
}
