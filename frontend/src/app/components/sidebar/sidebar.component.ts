import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-sidebar',
    templateUrl: './sidebar.component.html'
})
export class SidebarComponent {
    hidden = false;

    constructor(protected authService: AuthService) {
        const temp = localStorage.getItem('sidebar');
        if (temp != null) {
            this.hidden = JSON.parse(temp) === true;
        } else {
            this.hidden = false;
            localStorage.setItem('sidebar', String(this.hidden));
        }
    }

    protected toggle() {
        this.hidden = !this.hidden;
        localStorage.setItem('sidebar', String(this.hidden));
    }
}
