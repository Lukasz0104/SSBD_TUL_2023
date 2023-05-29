import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { canActivateAdmin } from '../shared/guards/admin.guard';
import { canActivateAuthenticated } from '../shared/guards/authentication.guard';
import { canActivateManagerAdmin } from '../shared/guards/manager-admin.guard';
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
        path: 'accounts/account',
        component: AccountComponent,
        data: {
            title: 'Account'
        },
        canActivate: [canActivateAdmin]
    },
    {
        path: 'accounts',
        component: AccountsComponent,
        data: {
            title: 'Accounts'
        },
        canActivate: [canActivateManagerAdmin]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MokRoutingModule {}
