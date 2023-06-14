import { Component, Input, OnInit } from '@angular/core';
import { PlaceService } from '../../services/place.service';
import { EMPTY, Observable } from 'rxjs';
import { PlaceOwner } from '../../model/place';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { PlaceOwnersAddComponent } from '../place-owners-add/place-owners-add.component';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';

@Component({
    selector: 'app-place-owners',
    templateUrl: './place-owners.component.html'
})
export class PlaceOwnersComponent implements OnInit {
    protected owners$: Observable<PlaceOwner[]> = EMPTY;

    @Input()
    placeId!: number;

    constructor(
        private placeService: PlaceService,
        private modalService: NgbModal
    ) {}

    ngOnInit(): void {
        this.getOwners();
    }

    getOwners() {
        this.owners$ = this.placeService.getPlaceOwners(this.placeId);
    }

    removeOwner(ownerDataId: number) {
        const modalRef = this.modalService.open(ConfirmActionComponent);
        const instance = modalRef.componentInstance as ConfirmActionComponent;

        instance.message = 'modal.confirm-action.place-owners-remove';
        instance.danger = '';
        modalRef.result.then((res: boolean): void => {
            if (res && this.placeId) {
                this.placeService
                    .removeOwner(ownerDataId, this.placeId)
                    .subscribe(() => this.getOwners());
            }
        });
    }

    addOwners() {
        const modalRef: NgbModalRef = this.modalService.open(
            PlaceOwnersAddComponent,
            { centered: true, scrollable: true, size: 'xl' }
        );
        modalRef.componentInstance.setPlace(this.placeId);
        modalRef.result
            .then((): void => {
                this.getOwners();
            })
            .catch(() => EMPTY);
    }
}
