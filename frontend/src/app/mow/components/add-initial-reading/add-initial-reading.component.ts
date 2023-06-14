import { Component, Input } from '@angular/core';
import { NgbActiveModal, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { threeDecimalPlacesValidator } from '../../../shared/validators/three-decimal-places.validator';
import { AuthService } from '../../../shared/services/auth.service';
import { AccountingRule } from '../../model/accounting-rule';

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
                threeDecimalPlacesValidator
            ]
        })
    });

    constructor(
        protected activeModal: NgbActiveModal,
        protected authService: AuthService
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
        if (this.addReadingForm.valid) {
            this.activeModal.close(
                this.addReadingForm.getRawValue().readingValue
            );
        }
    }

    protected get valueControl() {
        return this.addReadingForm.controls['readingValue'];
    }

    public get RULE(): typeof AccountingRule {
        return AccountingRule;
    }
}
