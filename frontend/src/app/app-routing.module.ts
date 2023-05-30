import { NgModule } from '@angular/core';
import { Route, RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { HomeComponent } from './components/home/home.component';
import { canActivateAuthenticated } from './shared/guards/authentication.guard';

const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        data: {
            title: 'Home'
        },
        loadChildren: () =>
            import('./auth/auth.module').then((m) => m.AuthModule)
    },
    {
        path: 'dashboard',
        component: DashboardComponent,
        data: {
            title: 'Dashboard'
        },
        canActivate: [canActivateAuthenticated],
        children: [
            {
                path: 'accounts',
                loadChildren: () =>
                    import('./mok/mok.module').then((m) => m.MokModule)
            },
            {
                path: '',
                loadChildren: () =>
                    import('./mow/mow.module').then((m) => m.MowModule)
            }
        ]
    }
];

const addProperty = (routes: Route): Route => {
    routes.runGuardsAndResolvers = 'always';
    if (routes.children)
        routes.children = routes.children.map((r) => addProperty(r));

    return routes;
};

@NgModule({
    imports: [
        RouterModule.forRoot(
            routes.map((r) => addProperty(r)),
            { onSameUrlNavigation: 'reload' }
        )
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {}
