import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CostsComponent } from './components/costs/costs.component';
import { canMatchManagerAdmin } from '../shared/guards/manager-admin.guard';

const routes: Routes = [
    {
        path: 'costs',
        component: CostsComponent,
        data: {
            title: 'Costs'
        },
        canMatch: [canMatchManagerAdmin]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MowRoutingModule {}
