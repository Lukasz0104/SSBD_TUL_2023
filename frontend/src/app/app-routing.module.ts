import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { canActivateLoginOrRegister } from './guards/guest.guard';
import { canActivateAuthenticated } from './guards/authentication.guard';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { ResetPasswordConfirmComponent } from './components/reset-password-confirm/reset-password-confirm.component';
import { HomeComponent } from './components/home/home.component';
import { AccountsComponent } from './components/accounts/accounts.component';
import { canActivateManagerAdmin } from './guards/manager-admin.guard';
import { ForcePasswordChangeOverrideComponent } from './components/force-password-change-override/force-password-change-override.component';

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
                path: 'accounts',
                component: AccountsComponent,
                data: {
                    title: 'Accounts'
                },
                canActivate: [canActivateManagerAdmin]
            }
        ]
    },
    {
        path: 'reset-password',
        component: ResetPasswordComponent,
        data: {
            title: 'Reset password'
        },
        canActivate: [canActivateLoginOrRegister]
    },
    {
        path: 'reset-password-confirm/:token',
        component: ResetPasswordConfirmComponent,
        data: {
            title: 'Confirm password reset'
        },
        canActivate: [canActivateLoginOrRegister]
    },
    {
        path: 'force-password-override/:token',
        component: ForcePasswordChangeOverrideComponent,
        data: {
            title: 'Override password change'
        },
        canActivate: [canActivateLoginOrRegister]
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {}
