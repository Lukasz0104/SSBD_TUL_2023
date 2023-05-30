import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { MowRoutingModule } from './mow-routing.module';
import { SharedModule } from '../shared/shared.module';

@NgModule({
    declarations: [],
    imports: [CommonModule, MowRoutingModule, SharedModule]
})
export class MowModule {}
