import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {
    HTTP_INTERCEPTORS,
    HttpClient,
    HttpClientModule
} from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbAccordionModule, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LoginComponent } from './components/login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ChooseAccessLevelComponent } from './components/modals/choose-access-level/choose-access-level.component';
import { JwtInterceptor } from './interceptors/jwt.interceptor';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastSectionComponent } from './components/toast-section/toast-section.component';
import { RefreshSessionComponent } from './components/modals/refresh-session/refresh-session.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { HomeComponent } from './components/home/home.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AccountComponent } from './components/account/account.component';
import { EditPersonalDataComponent } from './components/modals/edit-personal-data/edit-personal-data.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { ResetPasswordConfirmComponent } from './components/reset-password-confirm/reset-password-confirm.component';
import { ChangeLanguageComponent } from './components/change-language/change-language.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { ConfirmActionComponent } from './components/modals/confirm-action/confirm-action.component';
import { AccountsComponent } from './components/accounts/accounts.component';
import { ForcePasswordChangeOverrideComponent } from './components/force-password-change-override/force-password-change-override.component';
import { RegisterComponent } from './components/register/register.component';
import { ConfirmRegistrationComponent } from './components/confirm-registration/confirm-registration.component';
import { ChangeEmailComponent } from './components/change-email/change-email.component';
import { ConfirmEmailComponent } from './components/confirm-email/confirm-email.component';
import { ChangeActiveStatusComponent } from './components/change-active-status/change-active-status.component';

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        DashboardComponent,
        ChooseAccessLevelComponent,
        ToastSectionComponent,
        RefreshSessionComponent,
        RegisterComponent,
        ResetPasswordComponent,
        ResetPasswordConfirmComponent,
        NavbarComponent,
        SidebarComponent,
        ChangeLanguageComponent,
        ConfirmActionComponent,
        ForcePasswordChangeOverrideComponent,
        AccountsComponent,
        ProfileComponent,
        AccountComponent,
        ChangeEmailComponent,
        ConfirmEmailComponent,
        ChangeActiveStatusComponent,
        EditPersonalDataComponent,
        HomeComponent,
        ConfirmRegistrationComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        NgbModule,
        NgbAccordionModule,
        HttpClientModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        }),
        BrowserAnimationsModule,
        FormsModule
    ],
    providers: [
        { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
    ],
    bootstrap: [AppComponent]
})
export class AppModule {}

export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
    return new TranslateHttpLoader(http);
}
