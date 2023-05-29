import { NgModule } from '@angular/core';
import { Route, RouterModule, Routes } from '@angular/router';
import { AccountComponent } from './components/account/account.component';
import { AccountsComponent } from './components/accounts/accounts.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { HomeComponent } from './components/home/home.component';
import { ProfileComponent } from './components/profile/profile.component';
import { canActivateAdmin } from './shared/guards/admin.guard';
import { canActivateAuthenticated } from './shared/guards/authentication.guard';
import { canActivateManagerAdmin } from './shared/guards/manager-admin.guard';

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
                path: 'profile',
                component: ProfileComponent,
                data: {
                    title: 'My profile'
                },
                canActivate: [canActivateAuthenticated],
                children: [
                    {
                        path: 'change-password',
                        component: ChangePasswordComponent,
                        data: {
                            title: 'Change password'
                        },
                        canActivate: [canActivateAuthenticated]
                    }
                ]
            },
            {
                path: 'accounts/account',
                component: AccountComponent,
                data: {
                    title: 'Account'
                },
                canActivate: [canActivateAdmin]
            },
            {
                path: 'accounts',
                component: AccountsComponent,
                data: {
                    title: 'Accounts'
                },
                canActivate: [canActivateManagerAdmin]
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
