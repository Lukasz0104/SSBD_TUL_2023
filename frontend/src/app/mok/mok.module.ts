import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MokRoutingModule } from './mok-routing.module';
import { ActiveAccessLevelsPipe } from './pipes/active-access-levels.pipe';
import { SharedModule } from '../shared/shared.module';
import { TranslateModule } from '@ngx-translate/core';

import { AccountComponent } from './components/account/account.component';
import { AccountsComponent } from './components/accounts/accounts.component';
import { ChangeActiveStatusComponent } from './components/change-active-status/change-active-status.component';
import { ChangeEmailComponent } from './components/change-email/change-email.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { EditPersonalDataAsAdminComponent } from './components/edit-personal-data-as-admin/edit-personal-data-as-admin.component';
import { GrantAccessLevelComponent } from './components/grant-access-level/grant-access-level.component';
import { ProfileComponent } from './components/profile/profile.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { EditPersonalDataComponent } from './components/edit-personal-data/edit-personal-data.component';

@NgModule({
    declarations: [
        ActiveAccessLevelsPipe,
        AccountComponent,
        AccountsComponent,
        ChangeActiveStatusComponent,
        ChangeEmailComponent,
        ChangePasswordComponent,
        EditPersonalDataAsAdminComponent,
        GrantAccessLevelComponent,
        ProfileComponent,
        EditPersonalDataComponent
    ],
    imports: [
        CommonModule,
        MokRoutingModule,
        SharedModule,
        TranslateModule,
        FormsModule,
        ReactiveFormsModule
    ]
})
export class MokModule {}
