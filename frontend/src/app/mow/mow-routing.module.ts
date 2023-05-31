import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlaceDetailsComponent } from './components/place-details/place-details.component';
import { canMatchManager } from '../shared/guards/manager.guard';
import { CategoriesComponent } from './components/categories/categories.component';
import { OwnPlacesComponent } from './components/own-places/own-places.component';
import { canMatchOwner } from '../shared/guards/owner.guard';
import { WelcomeComponent } from './components/date/welcome.component';
import { canMatchManagerAdmin } from '../shared/guards/manager-admin.guard';

export const routes: Routes = [
    {
        path: '',
        component: WelcomeComponent,
        canActivate: [canMatchManagerAdmin]
    },
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
    },
    {
        path: 'your-places',
        component: OwnPlacesComponent,
        data: {
            title: 'Your places'
        },
        canMatch: [canMatchOwner]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MowRoutingModule {}
