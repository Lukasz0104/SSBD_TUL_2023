import { Component, Input } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal, NgbDate, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { RateService } from '../../services/rate.service';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';
import { AccountingRule } from '../../model/accounting-rule';
import { DatePipe } from '@angular/common';
import { twoDecimalPlacesValidator } from '../../../shared/validators/two-decimal-places.validator';
import { effectiveDateValidator } from '../../../shared/validators/effective-date.validator';

@Component({
    selector: 'app-add-rate',
    templateUrl: './add-rate.component.html'
})
export class AddRateComponent {
    @Input() categoryId: number | undefined;
    @Input() accountingRule: AccountingRule | undefined;
    today: NgbDate;
    initial: NgbDate;
    addRateForm = new FormGroup({
        rateValue: new FormControl(0.0, {
            validators: [
                Validators.required,
                Validators.min(0),
                twoDecimalPlacesValidator
            ]
        }),
        effectiveDate: new FormControl(new Date(), {
            validators: [Validators.required, effectiveDateValidator]
        })
    });

    constructor(
        public activeModal: NgbActiveModal,
        private modalService: NgbModal,
        private rateService: RateService,
        private datePipe: DatePipe
    ) {
        const today = new Date();
        this.today = new NgbDate(
            today.getFullYear(),
            ((today.getMonth() + 1) % 12) + 1,
            1
        );
        this.initial = new NgbDate(
            today.getFullYear(),
            ((today.getMonth() + 1) % 12) + 1,
            1
        );
    }

    confirm() {
        const modalRef = this.modalService.open(ConfirmActionComponent, {
            centered: true
        });
        const instance = modalRef.componentInstance as ConfirmActionComponent;

        instance.message = 'modal.confirm-action.add-rate';
        modalRef.closed.subscribe((res: boolean) => {
            if (res) {
                this.onSubmit();
            }
        });
    }

    onSubmit() {
        if (
            this.accountingRule != undefined &&
            this.categoryId != undefined &&
            this.effectiveDate.getRawValue() != null
        ) {
            const chosenDate = this.effectiveDate.getRawValue();
            const date = new Date();
            if (chosenDate != null) {
                date.setFullYear(
                    this.initial.year,
                    this.initial.month - 1,
                    this.initial.day
                );
            }
            this.rateService
                .addRate({
                    accountingRule: this.accountingRule.toString(),
                    effectiveDate: this.datePipe.transform(date, 'yyyy-MM-dd')!,
                    value: this.valueControl.getRawValue() ?? 0.0,
                    categoryId: this.categoryId
                })
                .subscribe((result) => {
                    if (result) {
                        this.activeModal.close();
                    }
                });
        }
    }

    protected get valueControl() {
        return this.addRateForm.controls.rateValue;
    }

    protected get effectiveDate() {
        return this.addRateForm.controls.effectiveDate;
    }
}
