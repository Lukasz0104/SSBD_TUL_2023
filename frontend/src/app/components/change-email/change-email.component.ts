import { Component } from '@angular/core';
import { AccountService } from '../../services/account.service';

@Component({
    selector: 'app-change-email',
    templateUrl: './change-email.component.html'
})
export class ChangeEmailComponent {
    constructor(private emailService: AccountService) {}

    onClick() {
        this.emailService.changeEmail().subscribe(() => {
            return;
        });
    }
}
