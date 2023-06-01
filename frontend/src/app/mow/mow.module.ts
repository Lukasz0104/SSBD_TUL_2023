import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { MowRoutingModule } from './mow-routing.module';
import { SharedModule } from '../shared/shared.module';
import { CostsComponent } from './components/costs/costs.component';
import { FormsModule } from '@angular/forms';

@NgModule({
    declarations: [CostsComponent],
    imports: [CommonModule, MowRoutingModule, SharedModule, FormsModule]
})
export class MowModule {}
