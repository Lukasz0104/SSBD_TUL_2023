import { CommonModule, DatePipe } from '@angular/common';
import { NgModule } from '@angular/core';

import { MowRoutingModule } from './mow-routing.module';
import { SharedModule } from '../shared/shared.module';
import { PlaceCategoriesComponent } from './components/place-categories/place-categories.component';
import { OwnPlacesComponent } from './components/own-places/own-places.component';
import { BuildingsComponent } from './components/buildings/buildings.component';
import { CategoriesComponent } from './components/categories/categories.component';
import { MetersComponent } from './components/meters/meters.component';
import { RatesByCategoryComponent } from './components/rates-by-category/rates-by-category.component';
import { CostsComponent } from './components/costs/costs.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { PlaceComponent } from './components/place/place.component';
import { PlaceReportsComponent } from './components/place-reports/place-reports.component';
import { MeterComponent } from './components/meter/meter.component';
import { AddRateComponent } from './components/add-rate/add-rate.component';
import { BuildingReportsComponent } from './components/building-reports/building-reports.component';
import { AddReadingComponent } from './components/add-reading/add-reading.component';
import { PlacesComponent } from './components/places/places.component';
import { PlaceEditComponent } from './components/place-edit/place-edit.component';
import { PlaceDetailsComponent } from './components/place-details/place-details.component';
import { PlaceAddCategoryComponent } from './components/place-add-category/place-add-category.component';
import { AddInitialReadingComponent } from './components/add-initial-reading/add-initial-reading.component';
import { CostComponent } from './components/cost/cost.component';
import { PlaceRatesComponent } from './components/place-rates/place-rates.component';
import { AddPlaceComponent } from './components/add-place/add-place.component';
import { PlaceOwnersComponent } from './components/place-owners/place-owners.component';
import { CommunityReportsComponent } from './components/community-reports/community-reports.component';
import { AddCostComponent } from './components/add-cost/add-cost.component';
import { PlaceOwnersAddComponent } from './components/place-owners-add/place-owners-add.component';
import { EditRateComponent } from './components/edit-rate/edit-rate.component';
import { ConfirmEmailComponent } from './components/confirm-email/confirm-email.component';

@NgModule({
    declarations: [
        CategoriesComponent,
        RatesByCategoryComponent,
        PlaceCategoriesComponent,
        OwnPlacesComponent,
        WelcomeComponent,
        MetersComponent,
        PlaceComponent,
        BuildingsComponent,
        BuildingReportsComponent,
        AddRateComponent,
        AddReadingComponent,
        MeterComponent,
        PlaceReportsComponent,
        PlacesComponent,
        CostsComponent,
        CostComponent,
        PlaceEditComponent,
        PlaceRatesComponent,
        PlaceDetailsComponent,
        PlaceAddCategoryComponent,
        AddInitialReadingComponent,
        AddPlaceComponent,
        PlaceOwnersComponent,
        CommunityReportsComponent,
        PlaceOwnersAddComponent,
        AddCostComponent,
        CommunityReportsComponent,
        EditRateComponent,
        ConfirmEmailComponent
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
