import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { MowRoutingModule } from './mow-routing.module';
import { SharedModule } from '../shared/shared.module';
import { BuildingsComponent } from './components/buildings/buildings.component';
import { CategoriesComponent } from './components/categories/categories.component';

@NgModule({
    declarations: [BuildingsComponent, CategoriesComponent],
    imports: [CommonModule, MowRoutingModule, SharedModule]
})
export class MowModule {}
