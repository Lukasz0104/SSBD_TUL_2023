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
import { BuildingReportsComponent } from './components/building-reports/building-reports.component';
import { PlacesComponent } from './components/places/places.component';
import { CommunityReportsComponent } from './components/community-reports/community-reports.component';
import { ConfirmEmailComponent } from './components/confirm-email/confirm-email.component';

export const routes: Routes = [
    {
        path: 'buildings',
        component: BuildingsComponent,
        canActivate: [canActivateAuthenticated]
    },
    {
        path: 'buildings/reports',
        component: BuildingReportsComponent,
        canActivate: [canActivateAuthenticated],
        title: 'Reports'
    },
    {
        path: 'buildings/:id',
        component: PlacesComponent,
        canMatch: [canMatchManager]
    },
    {
        path: '',
        component: WelcomeComponent,
        canActivate: [canActivateAuthenticated]
    },
    {
        path: 'categories',
        component: CategoriesComponent,
        title: 'Categories',
        canMatch: [canMatchManager]
    },
    {
        path: 'your-places',
        component: OwnPlacesComponent,
        title: 'Your places',
        canMatch: [canMatchOwner]
    },
    {
        path: 'costs',
        component: CostsComponent,
        title: 'Costs',
        canMatch: [canMatchManager]
    },
    {
        path: 'community-reports',
        component: CommunityReportsComponent,
        title: 'Community reports',
        canMatch: [canMatchManager]
    },
    {
        path: 'confirm-email/:id',
        component: ConfirmEmailComponent,
        data: {
            title: 'Confirm email'
        },
        canMatch: [canActivateAuthenticated]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MowRoutingModule {}
