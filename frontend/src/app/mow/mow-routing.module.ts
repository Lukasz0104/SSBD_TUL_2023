import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PlaceDetailsComponent } from './components/place-details/place-details.component';
import { canMatchManager } from '../shared/guards/manager.guard';
import { BuildingsComponent } from './components/buildings/buildings.component';
import { CategoriesComponent } from './components/categories/categories.component';
import { OwnPlacesComponent } from './components/own-places/own-places.component';
import { canMatchOwner } from '../shared/guards/owner.guard';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { canActivateAuthenticated } from '../shared/guards/authentication.guard';

export const routes: Routes = [
    { path: 'buildings', component: BuildingsComponent },
    {
        path: '',
        component: WelcomeComponent,
        canActivate: [canActivateAuthenticated]
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
