import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlaceDetailsComponent } from './components/place-details/place-details.component';
import { canMatchManager } from '../shared/guards/manager.guard';

export const routes: Routes = [
    {
        path: 'place-details',
        component: PlaceDetailsComponent,
        data: {
            title: 'Place details'
        },
        canActivate: [canMatchManager]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MowRoutingModule {}
