import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslateModule } from '@ngx-translate/core';
import {
    RECAPTCHA_SETTINGS,
    RecaptchaFormsModule,
    RecaptchaModule,
    RecaptchaSettings
} from 'ng-recaptcha';
import { AppConfigService } from '../shared/services/app-config.service';
import { SharedModule } from '../shared/shared.module';
import { AuthRoutingModule } from './auth-routing.module';
import { ConfirmEmailComponent } from './components/confirm-email/confirm-email.component';
import { ConfirmRegistrationComponent } from './components/confirm-registration/confirm-registration.component';
import { ForcePasswordChangeOverrideComponent } from './components/force-password-change-override/force-password-change-override.component';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { LoginComponent } from './components/login/login.component';
import { RecaptchaComponent } from './components/recaptcha/recaptcha.component';
import { RegisterComponent } from './components/register/register.component';
import { ResetPasswordConfirmComponent } from './components/reset-password-confirm/reset-password-confirm.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { UnlockAccountComponent } from './components/unlock-account/unlock-account.component';
import { CurrentRatesComponent } from './components/current-rates/current-rates.component';

@NgModule({
    declarations: [
        RegisterComponent,
        ResetPasswordComponent,
        ResetPasswordConfirmComponent,
        RecaptchaComponent,
        ConfirmEmailComponent,
        ConfirmRegistrationComponent,
        ForcePasswordChangeOverrideComponent,
        LandingPageComponent,
        LoginComponent,
        UnlockAccountComponent,
        CurrentRatesComponent
    ],
    imports: [
        CommonModule,
        AuthRoutingModule,
        SharedModule,
        NgbModule,
        ReactiveFormsModule,
        RecaptchaFormsModule,
        RecaptchaModule,
        TranslateModule
    ],
    providers: [
        {
            provide: RECAPTCHA_SETTINGS,
            deps: [AppConfigService],
            useFactory: (config: AppConfigService): RecaptchaSettings => ({
                siteKey: config.recaptchaKey
            })
        }
    ]
})
export class AuthModule {}
