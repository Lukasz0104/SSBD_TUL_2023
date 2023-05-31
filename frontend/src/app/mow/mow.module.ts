import { CommonModule, DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';

import { MowRoutingModule } from './mow-routing.module';
import { SharedModule } from '../shared/shared.module';
import { PlaceCategoriesComponent } from './components/place-categories/place-categories.component';
import { PlaceDetailsComponent } from './components/place-details/place-details.component';
import { OwnPlacesComponent } from './components/own-places/own-places.component';
import { CategoriesComponent } from './components/categories/categories.component';
import { RatesByCategoryComponent } from './components/rates-by-category/rates-by-category.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { WelcomeComponent } from './components/date/welcome.component';

@NgModule({
    declarations: [
        CategoriesComponent,
        RatesByCategoryComponent,
        PlaceCategoriesComponent,
        PlaceDetailsComponent,
        OwnPlacesComponent,
        WelcomeComponent
    ],
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
