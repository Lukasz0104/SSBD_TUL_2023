import {
    HTTP_INTERCEPTORS,
    HttpClient,
    HttpClientModule
} from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AccountComponent } from './components/account/account.component';
import { AccountsComponent } from './components/accounts/accounts.component';
import { ChangeActiveStatusComponent } from './components/change-active-status/change-active-status.component';
import { ChangeEmailComponent } from './components/change-email/change-email.component';
import { ChangeLanguageComponent } from './components/change-language/change-language.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { GuestNavbarComponent } from './components/guest-navbar/guest-navbar.component';
import { HomeComponent } from './components/home/home.component';
import { ChooseAccessLevelComponent } from './components/modals/choose-access-level/choose-access-level.component';
import { EditPersonalDataAsAdminComponent } from './components/modals/edit-personal-data-as-admin/edit-personal-data-as-admin.component';
import { EditPersonalDataComponent } from './components/modals/edit-personal-data/edit-personal-data.component';
import { GrantAccessLevelComponent } from './components/modals/grant-access-level/grant-access-level.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { ProfileComponent } from './components/profile/profile.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { ThemeSwitchComponent } from './components/theme-switch/theme-switch.component';
import { ToastSectionComponent } from './components/toast-section/toast-section.component';
import { JwtInterceptor } from './interceptors/jwt.interceptor';
import { ActiveAccessLevelsPipe } from './pipes/active-access-levels.pipe';
import { SharedModule } from './shared/shared.module';

@NgModule({
    declarations: [
        AppComponent,
        DashboardComponent,
        ChooseAccessLevelComponent,
        ToastSectionComponent,
        NavbarComponent,
        SidebarComponent,
        ChangeLanguageComponent,
        AccountsComponent,
        ProfileComponent,
        AccountComponent,
        ChangePasswordComponent,
        EditPersonalDataAsAdminComponent,
        ChangeEmailComponent,
        ChangeActiveStatusComponent,
        EditPersonalDataComponent,
        HomeComponent,
        GrantAccessLevelComponent,
        ActiveAccessLevelsPipe,
        GuestNavbarComponent,
        ThemeSwitchComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        BrowserAnimationsModule,
        FormsModule,
        SharedModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        })
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
