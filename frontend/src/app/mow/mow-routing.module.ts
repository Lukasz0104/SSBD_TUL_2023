import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CategoriesComponent } from './components/categories/categories.component';
import { canMatchManager } from '../shared/guards/manager.guard';
import { canMatchOwnerManager } from '../shared/guards/owner-manager.guard';
import { PlaceComponent } from './components/place/place.component';

export const routes: Routes = [
    {
        path: 'categories',
        component: CategoriesComponent,
        data: {
            title: 'Categories'
        },
        canMatch: [canMatchManager]
    },
    {
        path: 'place',
        component: PlaceComponent,
        data: {
            title: 'Place'
        },
        canMatch: [canMatchOwnerManager]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MowRoutingModule {}
