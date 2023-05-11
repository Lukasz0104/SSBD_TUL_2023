import { Component } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { ToastService } from '../../services/toast.service';

@Component({
    selector: 'app-change-email',
    templateUrl: './change-email.component.html'
})
export class ChangeEmailComponent {
    constructor(
        private emailService: AccountService,
        private toastService: ToastService
    ) {}

    onClick() {
        this.emailService.changeEmail().subscribe((result: boolean) => {
            if (result) {
                this.toastService.showSuccess('mail.sent.success'); //todo i18n
            } else {
                this.toastService.showDanger('mail.sent.failure'); //todo i18n
            }
        });
    }
}
