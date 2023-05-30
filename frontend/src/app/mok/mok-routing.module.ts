import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { canMatchAdmin } from '../shared/guards/admin.guard';
import { canActivateAuthenticated } from '../shared/guards/authentication.guard';
import { canMatchManagerAdmin } from '../shared/guards/manager-admin.guard';
import { AccountComponent } from './components/account/account.component';
import { AccountsComponent } from './components/accounts/accounts.component';
import { ProfileComponent } from './components/profile/profile.component';

const routes: Routes = [
    {
        path: 'profile',
        component: ProfileComponent,
        data: {
            title: 'My profile'
        },
        canActivate: [canActivateAuthenticated]
    },
    {
        path: 'account',
        component: AccountComponent,
        data: {
            title: 'Account'
        },
        canMatch: [canMatchAdmin]
    },
    {
        path: '',
        component: AccountsComponent,
        data: {
            title: 'Accounts'
        },
        canMatch: [canMatchManagerAdmin]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MokRoutingModule {}
