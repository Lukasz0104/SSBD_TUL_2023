import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CodeInputModule } from 'angular-code-input';
import {
    RECAPTCHA_SETTINGS,
    RecaptchaFormsModule,
    RecaptchaModule,
    RecaptchaSettings
} from 'ng-recaptcha';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AccountComponent } from './components/account/account.component';
import { AccountsComponent } from './components/accounts/accounts.component';
import { ChangeActiveStatusComponent } from './components/change-active-status/change-active-status.component';
import { ChangeEmailComponent } from './components/change-email/change-email.component';
import { ChangeLanguageComponent } from './components/change-language/change-language.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { ConfirmEmailComponent } from './components/confirm-email/confirm-email.component';
import { ConfirmRegistrationComponent } from './components/confirm-registration/confirm-registration.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ForcePasswordChangeOverrideComponent } from './components/force-password-change-override/force-password-change-override.component';
import { GuestNavbarComponent } from './components/guest-navbar/guest-navbar.component';
import { HomeComponent } from './components/home/home.component';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { LoginComponent } from './components/login/login.component';
import { ChooseAccessLevelComponent } from './components/modals/choose-access-level/choose-access-level.component';
import { EditPersonalDataAsAdminComponent } from './components/modals/edit-personal-data-as-admin/edit-personal-data-as-admin.component';
import { EditPersonalDataComponent } from './components/modals/edit-personal-data/edit-personal-data.component';
import { GrantAccessLevelComponent } from './components/modals/grant-access-level/grant-access-level.component';
import { TwoFactorAuthComponent } from './components/modals/two-factor-auth/two-factor-auth.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { ProfileComponent } from './components/profile/profile.component';
import { RecaptchaComponent } from './components/recaptcha/recaptcha.component';
import { RegisterComponent } from './components/register/register.component';
import { ResetPasswordConfirmComponent } from './components/reset-password-confirm/reset-password-confirm.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { ThemeSwitchComponent } from './components/theme-switch/theme-switch.component';
import { ToastSectionComponent } from './components/toast-section/toast-section.component';
import { UnlockAccountComponent } from './components/unlock-account/unlock-account.component';
import { JwtInterceptor } from './interceptors/jwt.interceptor';
import { ActiveAccessLevelsPipe } from './pipes/active-access-levels.pipe';
import { AppConfigService } from './shared/services/app-config.service';
import { SharedModule } from './shared/shared.module';

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        DashboardComponent,
        ChooseAccessLevelComponent,
        ToastSectionComponent,
        RegisterComponent,
        ResetPasswordComponent,
        ResetPasswordConfirmComponent,
        NavbarComponent,
        SidebarComponent,
        ChangeLanguageComponent,
        ForcePasswordChangeOverrideComponent,
        AccountsComponent,
        ProfileComponent,
        AccountComponent,
        ChangePasswordComponent,
        EditPersonalDataAsAdminComponent,
        ChangeEmailComponent,
        ConfirmEmailComponent,
        ChangeActiveStatusComponent,
        EditPersonalDataComponent,
        HomeComponent,
        ConfirmRegistrationComponent,
        GrantAccessLevelComponent,
        LandingPageComponent,
        TwoFactorAuthComponent,
        ConfirmRegistrationComponent,
        RecaptchaComponent,
        ActiveAccessLevelsPipe,
        GuestNavbarComponent,
        ThemeSwitchComponent,
        UnlockAccountComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        BrowserAnimationsModule,
        FormsModule,
        RecaptchaFormsModule,
        RecaptchaModule,
        CodeInputModule,
        SharedModule
    ],
    providers: [
        {
            provide: RECAPTCHA_SETTINGS,
            deps: [AppConfigService],
            useFactory: (config: AppConfigService): RecaptchaSettings => ({
                siteKey: config.recaptchaKey
            })
        },
        { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
    ],
    bootstrap: [AppComponent]
})
export class AppModule {}
