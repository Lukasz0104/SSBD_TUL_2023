import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { MowRoutingModule } from './mow-routing.module';
import { SharedModule } from '../shared/shared.module';
import { BuildingsComponent } from './components/buildings/buildings.component';

@NgModule({
    declarations: [BuildingsComponent],
    imports: [CommonModule, MowRoutingModule, SharedModule]
})
export class MowModule {}
