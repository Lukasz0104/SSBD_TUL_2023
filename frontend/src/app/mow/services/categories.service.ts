import { Injectable } from '@angular/core';
import { AppConfigService } from '../../shared/services/app-config.service';
import { HttpClient } from '@angular/common/http';
import { Category } from '../../shared/model/category';
import { RatePage } from '../../shared/model/rate-page';

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
        return this.http.get<RatePage>(
            `${this.appConfig.apiUrl}/categories/${categoryId}/rates?page=${page}&pageSize=${pageSize}`
        );
    }
}
