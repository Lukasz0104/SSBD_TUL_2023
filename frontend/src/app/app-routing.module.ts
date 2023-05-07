import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { canActivateLoginOrRegister } from './guards/guest.guard';
import { canActivateAuthenticated } from './guards/authentication.guard';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { ResetPasswordConfirmComponent } from './components/reset-password-confirm/reset-password-confirm.component';

const routes: Routes = [
    {
        path: 'login',
        component: LoginComponent,
        data: {
            title: 'Sign in'
        },
        canActivate: [canActivateLoginOrRegister]
    },
    {
        path: 'dashboard',
        component: DashboardComponent,
        data: {
            title: 'Dashboard'
        },
        canActivate: [canActivateAuthenticated]
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
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {}
