import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { ToastService } from '../../shared/services/toast.service';
import { AuthService } from '../../shared/services/auth.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AppConfigService } from '../../shared/services/app-config.service';
import { CostPage } from '../model/cost-page';

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
        return this.http.get<CostPage>(
            `${this.costsUrl}/?page=${page}&pageSize=${pageSize}&ascending=${sortDirection}&year=${year}&categoryName=${categoryName}`
        );
    }

    getYearsFromCosts() {
        return this.http.get<string[]>(`${this.costsUrl}/years`);
    }

    getCategoryNamesFromCosts() {
        return this.http.get<string[]>(`${this.costsUrl}/categories`);
    }
}
