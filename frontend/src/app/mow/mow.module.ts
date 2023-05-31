import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { MowRoutingModule } from './mow-routing.module';
import { SharedModule } from '../shared/shared.module';
import { OwnPlacesComponent } from './components/own-places/own-places.component';
import { CategoriesComponent } from './components/categories/categories.component';
import { PlaceComponent } from './components/place/place.component';

@NgModule({
    declarations: [CategoriesComponent, OwnPlacesComponent, PlaceComponent],
    imports: [CommonModule, MowRoutingModule, SharedModule]
})
export class MowModule {}
