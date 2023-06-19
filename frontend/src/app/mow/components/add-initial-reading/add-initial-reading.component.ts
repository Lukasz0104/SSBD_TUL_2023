import { Component, Input } from '@angular/core';
import { NgbActiveModal, NgbDate, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { threeDecimalPlacesValidator } from '../../../shared/validators/three-decimal-places.validator';
import { AuthService } from '../../../shared/services/auth.service';
import { AccountingRule } from '../../model/accounting-rule';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';

@Component({
    selector: 'app-add-initial-reading',
    templateUrl: './add-initial-reading.component.html'
})
export class AddInitialReadingComponent {
    today: NgbDate;
    initial: NgbDate;
    @Input() public value: boolean | undefined;
    @Input() public accountingRule: AccountingRule | undefined;
    addReadingForm = new FormGroup({
        readingValue: new FormControl(0.001, {
            validators: [
                Validators.required,
                Validators.min(0.001),
                Validators.max(999999999.999),
                threeDecimalPlacesValidator
            ]
        })
    });

    constructor(
        protected activeModal: NgbActiveModal,
        protected authService: AuthService,
        private modalService: NgbModal
    ) {
        const today = new Date();
        this.today = new NgbDate(
            today.getFullYear(),
            today.getMonth() + 1,
            today.getDate()
        );
        this.initial = new NgbDate(
            today.getFullYear(),
            ((today.getMonth() + 1) % 12) + 1,
            1
        );
    }

    onClick() {
        const modalRef = this.modalService.open(ConfirmActionComponent, {
            centered: true
        });
        const instance = modalRef.componentInstance as ConfirmActionComponent;

        instance.message = 'component.place.categories.confirm';
        instance.danger = 'component.place.categories.confirm-danger';
        modalRef.closed.subscribe((res: boolean) => {
            if (res) {
                this.activeModal.close(
                    this.addReadingForm.getRawValue().readingValue
                );
            }
        });
    }

    protected get valueControl() {
        return this.addReadingForm.controls['readingValue'];
    }

    public get RULE(): typeof AccountingRule {
        return AccountingRule;
    }
}
