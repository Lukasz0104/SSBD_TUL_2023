import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {
    HTTP_INTERCEPTORS,
    HttpClient,
    HttpClientModule
} from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
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
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { ResetPasswordConfirmComponent } from './components/reset-password-confirm/reset-password-confirm.component';
import { ChangeLanguageComponent } from './components/change-language/change-language.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { ConfirmActionComponent } from './components/modals/confirm-action/confirm-action.component';
import { AccountsComponent } from './components/accounts/accounts.component';
import { ForcePasswordChangeOverrideComponent } from './components/force-password-change-override/force-password-change-override.component';

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        DashboardComponent,
        ChooseAccessLevelComponent,
        ToastSectionComponent,
        RefreshSessionComponent,
        ResetPasswordComponent,
        ResetPasswordConfirmComponent,
        RefreshSessionComponent,
        NavbarComponent,
        SidebarComponent,
        HomeComponent,
        ChangeLanguageComponent,
        ConfirmActionComponent,
        HomeComponent,
        AccountsComponent,
        ForcePasswordChangeOverrideComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        NgbModule,
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
