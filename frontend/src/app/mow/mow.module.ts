import { CommonModule, DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';

import { MowRoutingModule } from './mow-routing.module';
import { SharedModule } from '../shared/shared.module';
import { CategoriesComponent } from './components/categories/categories.component';
import { RatesByCategoryComponent } from './components/rates-by-category/rates-by-category.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@NgModule({
    declarations: [CategoriesComponent, RatesByCategoryComponent],
    imports: [
        CommonModule,
        MowRoutingModule,
        SharedModule,
        ReactiveFormsModule,
        FormsModule
    ],
    providers: [DatePipe]
})
export class MowModule {}
