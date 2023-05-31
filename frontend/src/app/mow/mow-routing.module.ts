import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CategoriesComponent } from './components/categories/categories.component';
import { canMatchManager } from '../shared/guards/manager.guard';
import { BuildingsComponent } from './components/buildings/buildings.component';

export const routes: Routes = [
    { path: 'buildings', component: BuildingsComponent },
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
