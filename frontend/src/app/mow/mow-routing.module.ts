import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BuildingsComponent } from './components/buildings/buildings.component';

export const routes: Routes = [
    { path: 'buildings', component: BuildingsComponent }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MowRoutingModule {}
