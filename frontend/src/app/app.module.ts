import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbAccordionModule, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LoginComponent } from './components/login/login.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
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
import { AccountsComponent } from './components/accounts/accounts.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { EditPersonalDataAsAdminComponent } from './components/modals/edit-personal-data-as-admin/edit-personal-data-as-admin.component';

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        DashboardComponent,
        ChooseAccessLevelComponent,
        ToastSectionComponent,
        RefreshSessionComponent,
        NavbarComponent,
        SidebarComponent,
        HomeComponent,
        AccountsComponent,
        ProfileComponent,
        AccountComponent,
        EditPersonalDataComponent,
        ChangePasswordComponent,
        EditPersonalDataAsAdminComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        NgbModule,
        NgbAccordionModule,
        HttpClientModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        FormsModule
    ],
    providers: [
        { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
    ],
    bootstrap: [AppComponent]
})
export class AppModule {}
