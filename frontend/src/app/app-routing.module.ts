import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { canActivateAuthenticated } from './guards/authentication.guard';
import { HomeComponent } from './components/home/home.component';
import { canActivateLoginOrRegister } from './guards/guest.guard';

const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        data: {
            title: 'Home'
        },
        children: [
            {
                path: 'login',
                component: LoginComponent,
                data: {
                    title: 'Sign in'
                },
                canActivate: [canActivateLoginOrRegister]
            }
        ]
    },
    {
        path: 'register',
        component: RegisterComponent,
        canActivate: [canActivateLoginOrRegister]
    },
    {
        path: 'dashboard',
        component: DashboardComponent,
        data: {
            title: 'Dashboard'
        },
        canActivate: [canActivateAuthenticated],
        children: []
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {}
