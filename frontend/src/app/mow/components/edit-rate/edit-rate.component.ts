import { Component, Input } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { RateService } from '../../services/rate.service';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { twoDecimalPlacesValidator } from '../../../shared/validators/two-decimal-places.validator';

@Component({
    selector: 'app-edit-rate',
    templateUrl: './edit-rate.component.html'
})
export class EditRateComponent {
    @Input() rateId: number | undefined;
    editRateForm = new FormGroup({
        rateValue: new FormControl(0.0, {
            validators: [
                Validators.required,
                Validators.min(0),
                twoDecimalPlacesValidator
            ]
        })
    });

    constructor(
        public activeModal: NgbActiveModal,
        private modalService: NgbModal,
        private rateService: RateService
    ) {}

    confirm() {
        const modalRef = this.modalService.open(ConfirmActionComponent, {
            centered: true
        });
        const instance = modalRef.componentInstance as ConfirmActionComponent;

        instance.message = 'modal.confirm-action.edit-rate';
        modalRef.closed.subscribe((res: boolean) => {
            if (res) {
                this.onSubmit();
            }
        });
    }

    onSubmit() {
        if (this.rateId != undefined) {
            this.rateService
                .editRate(this.rateId, this.valueControl.getRawValue() ?? 0.0)
                .subscribe((result) => {
                    if (result) {
                        this.activeModal.close();
                    }
                });
        }
    }

    protected get valueControl() {
        return this.editRateForm.controls.rateValue;
    }
}
