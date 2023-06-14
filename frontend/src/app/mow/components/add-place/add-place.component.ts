import { Component, Input } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';
import { filter, mergeMap } from 'rxjs';
import { NonNullableFormBuilder, Validators } from '@angular/forms';
import { PlaceService } from '../../services/place.service';
import { ToastService } from '../../../shared/services/toast.service';

@Component({
    selector: 'app-add-place',
    templateUrl: './add-place.component.html'
})
export class AddPlaceComponent {
    @Input()
    buildingId!: number;

    protected addPlaceForm = this.fb.group({
        placeNumber: this.fb.control(1, [
            Validators.required,
            Validators.min(1)
        ]),
        squareFootage: this.fb.control(0.0, [
            Validators.required,
            Validators.min(0.01)
        ]),
        residentsNumber: this.fb.control(0, [
            Validators.required,
            Validators.min(0)
        ])
    });

    constructor(
        protected activeModal: NgbActiveModal,
        private modalService: NgbModal,
        private fb: NonNullableFormBuilder,
        private placeService: PlaceService,
        private toastService: ToastService
    ) {}

    addPlace() {
        const confirmModal = this.modalService.open(ConfirmActionComponent);
        const confirmComponent =
            confirmModal.componentInstance as ConfirmActionComponent;

        confirmComponent.message = 'modal.confirm-action.add-place';

        confirmModal.closed
            .pipe(
                filter((result) => result),
                mergeMap(() =>
                    this.placeService.addPlace({
                        buildingId: this.buildingId,
                        ...this.addPlaceForm.getRawValue()
                    })
                )
            )
            .subscribe((message) => {
                if (message == null) {
                    this.toastService.showSuccess('modal.add-place.success');
                    this.activeModal.close();
                } else {
                    this.toastService.showDanger(`modal.add-place.${message}`);
                }
            });
    }

    protected get placeNumberCtl() {
        return this.addPlaceForm.controls.placeNumber;
    }

    protected get squareFootageCtl() {
        return this.addPlaceForm.controls.squareFootage;
    }

    protected get residentsNumberCtl() {
        return this.addPlaceForm.controls.residentsNumber;
    }
}
