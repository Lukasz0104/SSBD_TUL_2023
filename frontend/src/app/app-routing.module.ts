import { NgModule } from '@angular/core';
import { Route, RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { canActivateAuthenticated } from './guards/authentication.guard';
import { canActivateLoginOrRegister } from './guards/guest.guard';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { ResetPasswordConfirmComponent } from './components/reset-password-confirm/reset-password-confirm.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AccountComponent } from './components/account/account.component';
import { canActivateAdmin } from './guards/admin.guard';
import { AccountsComponent } from './components/accounts/accounts.component';
import { canActivateManagerAdmin } from './guards/manager-admin.guard';
import { ConfirmEmailComponent } from './components/confirm-email/confirm-email.component';
import { ConfirmRegistrationComponent } from './components/confirm-registration/confirm-registration.component';
import { ForcePasswordChangeOverrideComponent } from './components/force-password-change-override/force-password-change-override.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { canActivateGuestWithRedirect } from './guards/redirecting-guest.guard';
import { UnlockAccountComponent } from './components/unlock-account/unlock-account.component';

const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        data: {
            title: 'Home'
        },
        children: [
            {
                path: '',
                component: LandingPageComponent,
                data: {
                    title: 'eBok'
                },
                canActivate: [canActivateGuestWithRedirect]
            },
            {
                path: 'login',
                component: LoginComponent,
                data: {
                    title: 'Sign in'
                },
                canActivate: [canActivateLoginOrRegister]
            },
            {
                path: 'confirm-email/:id',
                component: ConfirmEmailComponent,
                data: {
                    title: 'Change email'
                },
                canActivate: [canActivateAuthenticated]
            },
            {
                path: 'register',
                data: {
                    title: 'Register'
                },
                component: RegisterComponent,
                canActivate: [canActivateLoginOrRegister]
            },
            {
                path: 'confirm-account',
                component: ConfirmRegistrationComponent
            },
            {
                path: 'unlock-account',
                component: UnlockAccountComponent
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
                }
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
