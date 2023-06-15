import { Component, OnInit } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NonNullableFormBuilder, Validators } from '@angular/forms';
import { ToastService } from '../../../shared/services/toast.service';
import { CostsService } from '../../services/costs.service';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';
import { Category } from '../../model/category';
import { CategoriesService } from '../../services/categories.service';

@Component({
    selector: 'app-add-cost',
    templateUrl: './add-cost.component.html'
})
export class AddCostComponent implements OnInit {
    addCostForm = this.fb.group({
        month: this.fb.control(-1, [Validators.required, Validators.min(0)]),
        year: this.fb.control(2023, [Validators.required, Validators.min(0)]),
        category: this.fb.control(-1, [Validators.required, Validators.min(0)]),
        totalConsumption: this.fb.control(-0.01, [
            Validators.required,
            Validators.min(0.01)
        ]),
        realRate: this.fb.control(-0.01, [
            Validators.required,
            Validators.min(0.01)
        ])
    });

    categoryList: Category[] | undefined;
    months = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11].map(
        (x) => new Date(1, x, 1)
    );

    constructor(
        protected activeModal: NgbActiveModal,
        private modalService: NgbModal,
        private fb: NonNullableFormBuilder,
        private costService: CostsService,
        private toastService: ToastService,
        private categoriesService: CategoriesService
    ) {}

    ngOnInit(): void {
        this.categoriesService.getAllCategories().subscribe((categories) => {
            this.categoryList = categories;
        });
    }

    confirm() {
        const modalRef = this.modalService.open(ConfirmActionComponent, {
            centered: true
        });
        const instance = modalRef.componentInstance as ConfirmActionComponent;

        instance.message = 'modal.confirm-action.add-cost';
        modalRef.closed.subscribe((res: boolean) => {
            if (res) {
                this.onSubmit();
            }
        });
    }

    onSubmit() {
        if (this.addCostForm.valid) {
            this.costService
                .addCost(
                    this.monthControl.getRawValue() + 1,
                    this.yearControl.getRawValue(),
                    this.categoryControl.getRawValue(),
                    this.totalConsumptionControl.getRawValue(),
                    this.realRateControl.getRawValue()
                )
                .subscribe((result) => {
                    if (result) {
                        this.activeModal.close();
                    }
                });
        }
    }

    get monthControl() {
        return this.addCostForm.controls.month;
    }

    get yearControl() {
        return this.addCostForm.controls.year;
    }
    get totalConsumptionControl() {
        return this.addCostForm.controls.totalConsumption;
    }

    get categoryControl() {
        return this.addCostForm.controls.category;
    }

    get realRateControl() {
        return this.addCostForm.controls.realRate;
    }
}
