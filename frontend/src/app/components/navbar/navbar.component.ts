import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html'
})
export class NavbarComponent {
    constructor(protected authService: AuthService) {}

    logout() {
        this.authService.logout();
    }
}
