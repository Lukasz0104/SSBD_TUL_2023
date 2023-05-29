import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TruncatePipe } from './pipes/truncate.pipe';
import { ConfirmActionComponent } from './components/confirm-action/confirm-action.component';
import { TranslateModule } from '@ngx-translate/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RefreshSessionComponent } from './components/refresh-session/refresh-session.component';
import { TwoFactorAuthComponent } from './components/two-factor-auth/two-factor-auth.component';
import { CodeInputModule } from 'angular-code-input';

@NgModule({
    declarations: [
        TruncatePipe,
        ConfirmActionComponent,
        RefreshSessionComponent,
        TwoFactorAuthComponent
    ],
    imports: [CommonModule, NgbModule, TranslateModule, CodeInputModule],
    exports: [TruncatePipe, ConfirmActionComponent, TranslateModule, NgbModule]
})
export class SharedModule {}
