import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { AccountService } from '../../services/account.service';

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html'
})
export class NavbarComponent {
    constructor(
        protected authService: AuthService,
        private accountService: AccountService
    ) {}

    logout() {
        this.authService.logout();
    }

    changeAccessLevel() {
        this.accountService.changeAccessLevel();
    }
}
