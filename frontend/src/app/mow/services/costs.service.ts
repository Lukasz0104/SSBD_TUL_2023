import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { ToastService } from '../../shared/services/toast.service';
import { AuthService } from '../../shared/services/auth.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppConfigService } from '../../shared/services/app-config.service';
import { Cost } from '../model/cost';
import { Page } from '../../shared/model/page';
import { catchError, map, of, tap } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class CostsService {
    constructor(
        private http: HttpClient,
        private router: Router,
        private toastService: ToastService,
        private authService: AuthService,
        private modalService: NgbModal,
        private appConfig: AppConfigService
    ) {}
    private readonly costsUrl = `${this.appConfig.apiUrl}/costs`;

    getAllCosts(
        page: number,
        pageSize: number,
        sortDirection: boolean,
        year: string | undefined,
        categoryName: string | undefined
    ) {
        return this.http.get<Page<Cost>>(this.costsUrl, {
            params: {
                page,
                pageSize,
                year: year ?? '',
                categoryName: categoryName ?? '',
                ascending: sortDirection
            }
        });
    }

    getYearsFromCosts() {
        return this.http.get<string[]>(`${this.costsUrl}/years`);
    }

    getCategoryNamesFromCosts() {
        return this.http.get<string[]>(`${this.costsUrl}/categories`);
    }

    getCost(id: number) {
        return this.http.get<Cost>(`${this.costsUrl}/${id}`);
    }

    addCost(
        month: number,
        year: number,
        categoryId: number,
        totalConsumption: number,
        realRate: number
    ) {
        return this.http
            .post(
                `${this.costsUrl}`,
                {
                    year: year,
                    month: month,
                    categoryId: categoryId,
                    totalConsumption: totalConsumption,
                    realRate: realRate
                },
                {
                    observe: 'response'
                }
            )
            .pipe(
                tap(() => {
                    this.toastService.showSuccess('toast.cost.add-success'); //TODO I18n
                }),
                map(() => true),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        'toast.cost.add-fail', //TODO I18n
                        'toast.cost', //TODO I18n
                        err
                    );
                    return of(true);
                })
            );
    }
    handleError(
        genericMessageKey: string,
        method: string,
        response: HttpErrorResponse
    ) {
        if (response.status == 500 || response.error.message == null) {
            this.toastService.showDanger(genericMessageKey);
        } else {
            this.toastService.showDanger(method + '.' + response.error.message);
        }
    }
}
