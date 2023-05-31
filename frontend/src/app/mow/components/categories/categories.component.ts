import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../../../shared/services/auth.service';
import { CategoriesService } from '../../services/categories.service';
import { Category } from '../../../shared/model/category';

@Component({
    selector: 'app-categories',
    templateUrl: './categories.component.html'
})
export class CategoriesComponent implements OnInit {
    categories$: Observable<Category[]> | undefined;

    constructor(
        private categoriesService: CategoriesService,
        protected authService: AuthService
    ) {}

    getCategories() {
        this.categories$ = this.categoriesService.getAllCategories();
    }

    ngOnInit() {
        this.getCategories();
    }
}
