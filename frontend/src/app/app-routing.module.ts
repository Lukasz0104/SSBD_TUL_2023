import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { canActivateLoginOrRegister } from './guards/guest.guard';
import { canActivateAuthenticated } from './guards/authentication.guard';
import { HomeComponent } from './components/home/home.component';

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
