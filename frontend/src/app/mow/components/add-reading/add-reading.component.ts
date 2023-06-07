import { Component, Input } from '@angular/core';
import { NgbActiveModal, NgbDate, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { RateService } from '../../services/rate.service';
import { DatePipe } from '@angular/common';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';
import { AuthService } from '../../../shared/services/auth.service';
import { ReadingService } from '../../services/reading.service';
import { threeDecimalPlacesValidator } from '../../../shared/validators/three-decimal-places.validator';

@Component({
    selector: 'app-add-reading',
    templateUrl: './add-reading.component.html'
})
export class AddReadingComponent {
    @Input() meterId: number | undefined;
    today: NgbDate;
    initial: NgbDate;
    addReadingForm = new FormGroup({
        readingValue: new FormControl(0.001, {
            validators: [
                Validators.required,
                Validators.min(0.001),
                threeDecimalPlacesValidator
            ]
        }),
        date: new FormControl(new Date(), {
            validators: [Validators.required]
        })
    });

    constructor(
        public activeModal: NgbActiveModal,
        private modalService: NgbModal,
        private rateService: RateService,
        private datePipe: DatePipe,
        protected authService: AuthService,
        private readingService: ReadingService
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

    confirm() {
        const modalRef = this.modalService.open(ConfirmActionComponent, {
            centered: true
        });
        const instance = modalRef.componentInstance as ConfirmActionComponent;

        instance.message = 'modal.confirm-action.add-reading';
        modalRef.closed.subscribe((res: boolean) => {
            if (res) {
                this.onSubmit();
            }
        });
    }

    onSubmit() {
        const value = this.valueControl.getRawValue();
        if (this.meterId && value) {
            const date = new Date();
            if (this.today != null) {
                date.setFullYear(
                    this.today.year,
                    this.today.month - 1,
                    this.today.day
                );
            }

            if (this.authService.isOwner()) {
                this.readingService
                    .addReadingAsOwner(this.meterId, value)
                    .subscribe((result) => {
                        if (result) {
                            this.activeModal.close();
                        }
                    });
            } else {
                this.readingService
                    .addReadingAsManager(
                        this.meterId,
                        value,
                        this.datePipe.transform(date, 'yyyy-MM-dd')!
                    )
                    .subscribe((result) => {
                        if (result) {
                            this.activeModal.close();
                        }
                    });
            }
        }
    }

    protected get valueControl() {
        return this.addReadingForm.controls['readingValue'];
    }

    protected get date() {
        return this.addReadingForm.controls['date'];
    }
}
