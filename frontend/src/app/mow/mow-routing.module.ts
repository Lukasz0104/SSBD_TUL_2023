import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CategoriesComponent } from './components/categories/categories.component';
import { canMatchManager } from '../shared/guards/manager.guard';
import { canMatchOwnerManager } from '../shared/guards/owner-manager.guard';
import { PlaceComponent } from './components/place/place.component';
import { OwnPlacesComponent } from './components/own-places/own-places.component';
import { canMatchOwner } from '../shared/guards/owner.guard';

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
        path: 'your-places',
        component: OwnPlacesComponent,
        data: {
            title: 'Your places'
        },
        canMatch: [canMatchOwner],
        children: [
            {
                path: 'place',
                component: PlaceComponent,
                data: {
                    title: 'Place'
                },
                canMatch: [canMatchOwnerManager]
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MowRoutingModule {}
