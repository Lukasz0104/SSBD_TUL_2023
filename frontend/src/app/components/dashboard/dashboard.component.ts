import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html'
})
export class DashboardComponent {
    constructor(private authService: AuthService) {}

    getCurrentGroup() {
        return this.authService.getCurrentGroup();
    }
}
