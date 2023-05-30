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
import { ChangeLanguageComponent } from './components/change-language/change-language.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { GuestNavbarComponent } from './components/guest-navbar/guest-navbar.component';
import { HomeComponent } from './components/home/home.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { ThemeSwitchComponent } from './components/theme-switch/theme-switch.component';
import { ToastSectionComponent } from './components/toast-section/toast-section.component';
import { JwtInterceptor } from './interceptors/jwt.interceptor';
import { SharedModule } from './shared/shared.module';

@NgModule({
    declarations: [
        AppComponent,
        DashboardComponent,
        ToastSectionComponent,
        NavbarComponent,
        SidebarComponent,
        ChangeLanguageComponent,
        HomeComponent,
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
