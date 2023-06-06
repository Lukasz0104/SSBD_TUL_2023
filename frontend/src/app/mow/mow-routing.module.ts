import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { canMatchManager } from '../shared/guards/manager.guard';
import { BuildingsComponent } from './components/buildings/buildings.component';
import { CategoriesComponent } from './components/categories/categories.component';
import { OwnPlacesComponent } from './components/own-places/own-places.component';
import { canMatchOwner } from '../shared/guards/owner.guard';
import { WelcomeComponent } from './components/welcome/welcome.component';
import { canActivateAuthenticated } from '../shared/guards/authentication.guard';
import { CostsComponent } from './components/costs/costs.component';
import { PlacesComponent } from './components/places/places.component';

export const routes: Routes = [
    { path: 'buildings/:id/places', component: PlacesComponent },
    { path: 'buildings', component: BuildingsComponent },
    {
        path: '',
        component: WelcomeComponent,
        canActivate: [canActivateAuthenticated]
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
    },
    {
        path: 'costs',
        component: CostsComponent,
        data: {
            title: 'Costs'
        },
        canMatch: [canMatchManager]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MowRoutingModule {}
