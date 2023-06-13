import { Injectable } from '@angular/core';
import { AppConfigService } from '../../shared/services/app-config.service';
import { HttpClient } from '@angular/common/http';
import { Category } from '../model/category';
import { Page } from '../../shared/model/page';
import { Rate } from '../model/rate';

@Injectable({
    providedIn: 'root'
})
export class CategoriesService {
    constructor(
        private appConfig: AppConfigService,
        private http: HttpClient
    ) {}

    getAllCategories() {
        return this.http.get<Category[]>(`${this.appConfig.apiUrl}/categories`);
    }

    getRatesByCategory(categoryId: number, page: number, pageSize: number) {
        return this.http.get<Page<Rate>>(
            `${this.appConfig.apiUrl}/categories/${categoryId}/rates?page=${page}&pageSize=${pageSize}`
        );
    }
}
