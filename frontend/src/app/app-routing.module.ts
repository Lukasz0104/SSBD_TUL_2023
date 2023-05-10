import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { canActivateLoginOrRegister } from './guards/guest.guard';
import { canActivateAuthenticated } from './guards/authentication.guard';
import { HomeComponent } from './components/home/home.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AccountComponent } from './components/account/account.component';
import { canActivateAdmin } from './guards/admin.guard';
import { AccountsComponent } from './components/accounts/accounts.component';
import { canActivateManagerAdmin } from './guards/manager-admin.guard';
import { ChangePasswordComponent } from './components/change-password/change-password.component';

const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        data: {
            title: 'Home'
        },
        children: [
            {
                path: 'login',
                component: LoginComponent,
                data: {
                    title: 'Sign in'
                },
                canActivate: [canActivateLoginOrRegister]
            }
        ]
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

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {}
