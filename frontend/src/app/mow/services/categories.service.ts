import { Injectable } from '@angular/core';
import { AppConfigService } from '../../shared/services/app-config.service';
import { HttpClient } from '@angular/common/http';
import { Category } from '../../shared/model/category';

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
}
