import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlaceDetailsComponent } from './components/place-details/place-details.component';
import { canMatchManager } from '../shared/guards/manager.guard';
import { CategoriesComponent } from './components/categories/categories.component';

export const routes: Routes = [
    {
        path: 'place-details',
        component: PlaceDetailsComponent,
        data: {
            title: 'Place details'
        },
        canActivate: [canMatchManager]
    },
    {
        path: 'categories',
        component: CategoriesComponent,
        data: {
            title: 'Categories'
        },
        canMatch: [canMatchManager]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MowRoutingModule {}
