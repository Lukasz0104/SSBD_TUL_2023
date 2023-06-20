import { Component, Input, OnInit } from '@angular/core';
import { CategoriesService } from '../../services/categories.service';
import { EMPTY, Observable, tap } from 'rxjs';
import { Category } from '../../model/category';
import { AccountingRule } from '../../model/accounting-rule';
import { Rate } from '../../model/rate';
import { DatePipe } from '@angular/common';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AddRateComponent } from '../add-rate/add-rate.component';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';
import { RateService } from '../../../shared/services/rate.service';
import { Page } from '../../../shared/model/page';
import { EditRateComponent } from '../edit-rate/edit-rate.component';

@Component({
    selector: 'app-rates-by-category',
    templateUrl: './rates-by-category.component.html'
})
export class RatesByCategoryComponent implements OnInit {
    @Input() category$: Observable<Category | null> | undefined;
    category: Category | null | undefined;
    rates$: Observable<Page<Rate>> | undefined;
    currentRateId = -1;
    page = 1;
    pageSize = 10;
    currentDate = new Date();

    constructor(
        private categoriesService: CategoriesService,
        private datePipe: DatePipe,
        private modalService: NgbModal,
        private rateService: RateService
    ) {}

    ngOnInit(): void {
        this.category$?.subscribe((category) => {
            if (category != null) {
                this.category = category;
                this.getRatesByCategory();
            }
        });
    }

    getRatesByCategory() {
        this.currentDate = new Date();
        if (this.category != null) {
            this.rates$ = this.categoriesService
                .getRatesByCategory(
                    this.category?.id,
                    this.page - 1,
                    this.pageSize
                )
                .pipe(
                    tap((ratePage) => {
                        const currentDate = new Date();
                        const futureRates = ratePage.data
                            .filter((rate) => {
                                return (
                                    new Date(rate.effectiveDate) < currentDate
                                );
                            })
                            .sort((a, b) => {
                                return (
                                    new Date(b.effectiveDate).getTime() -
                                    new Date(a.effectiveDate).getTime()
                                );
                            });
                        if (futureRates.length > 0) {
                            this.currentRateId = futureRates[0].id;
                        }
                    })
                );
        }
    }

    getCreated(rate: Rate) {
        if (rate.createdTime == null || rate.createdBy == null) {
            return '-';
        }
        return `${this.datePipe.transform(
            rate.createdTime,
            'dd/MM/yy HH:mm:ss'
        )}, ${rate.createdBy}`;
    }

    getUpdated(rate: Rate) {
        if (rate.updatedTime == null || rate.updatedBy == null) {
            return '-';
        }
        return `${this.datePipe.transform(
            rate.updatedTime.toLocaleString(),
            'dd/MM/yy HH:mm:ss'
        )}, ${rate.updatedBy}`;
    }

    addRate(rate: Rate): void {
        const modalRef: NgbModalRef = this.modalService.open(AddRateComponent, {
            centered: true,
            scrollable: true
        });
        modalRef.componentInstance.categoryId = this.category?.id;
        modalRef.componentInstance.accountingRule = rate.accountingRule;
        modalRef.result
            .then((): void => {
                this.getRatesByCategory();
            })
            .catch(() => {
                this.modalService.dismissAll();
                return EMPTY;
            });
    }

    isFuture(date: Date) {
        return new Date(date).getTime() - this.currentDate.getTime() > 0;
    }

    confirmRemoveRate(id: number) {
        const modalRef = this.modalService.open(ConfirmActionComponent, {
            centered: true
        });
        const instance = modalRef.componentInstance as ConfirmActionComponent;

        instance.message = 'modal.confirm-action.remove-rate';
        instance.danger = 'modal.confirm-action.remove-rate-danger';
        modalRef.closed.subscribe((res: boolean) => {
            if (res) {
                this.removeRate(id);
            }
        });
    }

    editRate(id: number) {
        const modalRef = this.modalService.open(EditRateComponent, {
            centered: true
        });

        const instance = modalRef.componentInstance as EditRateComponent;
        instance.rateId = id;
    }

    removeRate(id: number) {
        this.rateService.removeRate(id).subscribe((result) => {
            if (result) {
                this.getRatesByCategory();
            }
        });
    }

    protected readonly AccountingRule = AccountingRule;
}
