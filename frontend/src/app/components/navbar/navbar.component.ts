import { Component } from '@angular/core';
import { AuthService } from '../../shared/services/auth.service';
import { AccountService } from '../../shared/services/account.service';

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.css']
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
