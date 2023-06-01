import { CommonModule, DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';

import { MowRoutingModule } from './mow-routing.module';
import { SharedModule } from '../shared/shared.module';
import { PlaceCategoriesComponent } from './components/place-categories/place-categories.component';
import { OwnPlacesComponent } from './components/own-places/own-places.component';
import { BuildingsComponent } from './components/buildings/buildings.component';
import { CategoriesComponent } from './components/categories/categories.component';
import { RatesByCategoryComponent } from './components/rates-by-category/rates-by-category.component';
import { CostsComponent } from './components/costs/costs.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { PlaceComponent } from './components/place/place.component';

@NgModule({
    declarations: [
        CategoriesComponent,
        RatesByCategoryComponent,
        PlaceCategoriesComponent,
        OwnPlacesComponent,
        WelcomeComponent,
        PlaceComponent,
        BuildingsComponent,
        CostsComponent
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
