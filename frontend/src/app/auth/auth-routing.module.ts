import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { canActivateLoginOrRegister } from '../shared/guards/guest.guard';
import { ConfirmRegistrationComponent } from './components/confirm-registration/confirm-registration.component';
import { RegisterComponent } from './components/register/register.component';
import { ResetPasswordConfirmComponent } from './components/reset-password-confirm/reset-password-confirm.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { UnlockAccountComponent } from './components/unlock-account/unlock-account.component';
import { canActivateAuthenticated } from '../shared/guards/authentication.guard';
import { canActivateGuestWithRedirect } from '../shared/guards/redirecting-guest.guard';
import { ConfirmEmailComponent } from './components/confirm-email/confirm-email.component';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { LoginComponent } from './components/login/login.component';
import { ForcePasswordChangeOverrideComponent } from './components/force-password-change-override/force-password-change-override.component';
import { canActivateForcedPasswordOverride } from '../shared/guards/forced-password-override.guard';

const routes: Routes = [
    {
        path: '',
        component: LandingPageComponent,
        title: 'eBok',
        canActivate: [canActivateGuestWithRedirect]
    },
    {
        path: 'login',
        component: LoginComponent,
        title: 'Sign in',
        canActivate: [canActivateLoginOrRegister]
    },
    {
        path: 'confirm-email/:id',
        component: ConfirmEmailComponent,
        title: 'Change email',
        canActivate: [canActivateAuthenticated]
    },
    {
        path: 'register',
        title: 'Register',
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
        title: 'Reset password',
        canActivate: [canActivateLoginOrRegister]
    },
    {
        path: 'reset-password-confirm/:token',
        component: ResetPasswordConfirmComponent,
        title: 'Confirm password reset',
        canActivate: [canActivateLoginOrRegister]
    },
    {
        path: 'forced-password-override/:token',
        component: ForcePasswordChangeOverrideComponent,
        title: 'Override password change',
        canActivate: [canActivateForcedPasswordOverride]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class AuthRoutingModule {}
