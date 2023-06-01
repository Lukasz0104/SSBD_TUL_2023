import { Component } from '@angular/core';
import { AuthService } from '../../../shared/services/auth.service';

@Component({
    selector: 'app-date',
    templateUrl: './welcome.component.html'
})
export class WelcomeComponent {
    protected date = new Date();

    constructor(protected authService: AuthService) {}
}
