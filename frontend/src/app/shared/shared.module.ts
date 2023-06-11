import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TruncatePipe } from './pipes/truncate.pipe';
import { ConfirmActionComponent } from './components/confirm-action/confirm-action.component';
import { TranslateModule } from '@ngx-translate/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RefreshSessionComponent } from './components/refresh-session/refresh-session.component';
import { TwoFactorAuthComponent } from './components/two-factor-auth/two-factor-auth.component';
import { CodeInputModule } from 'angular-code-input';
import { ChooseAccessLevelComponent } from './components/choose-access-level/choose-access-level.component';
import { AddressPipe } from './pipes/address.pipe';

@NgModule({
    declarations: [
        TruncatePipe,
        ConfirmActionComponent,
        RefreshSessionComponent,
        TwoFactorAuthComponent,
        ChooseAccessLevelComponent,
        AddressPipe
    ],
    imports: [CommonModule, NgbModule, TranslateModule, CodeInputModule],
    exports: [
        TruncatePipe,
        ConfirmActionComponent,
        TranslateModule,
        NgbModule,
        AddressPipe
    ]
})
export class SharedModule {}
