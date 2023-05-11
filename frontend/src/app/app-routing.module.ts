import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { canActivateAuthenticated } from './guards/authentication.guard';
import { canActivateLoginOrRegister } from './guards/guest.guard';
import { ProfileComponent } from './components/profile/profile.component';
import { AccountComponent } from './components/account/account.component';
import { canActivateAdmin } from './guards/admin.guard';
import { AccountsComponent } from './components/accounts/accounts.component';
import { canActivateManagerAdmin } from './guards/manager-admin.guard';
import { ConfirmRegistrationComponent } from './components/confirm-registration/confirm-registration.component';

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
            },
            {
                path: 'register',
                title: 'Register',
                component: RegisterComponent,
                canActivate: [canActivateLoginOrRegister]
            },
            {
                path: 'confirm-account',
                component: ConfirmRegistrationComponent // TODO add guard to check if token is present
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
                canActivate: [canActivateAuthenticated]
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
