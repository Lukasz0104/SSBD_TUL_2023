import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from '../../../services/auth.service';

@Component({
    selector: 'app-two-factor-auth',
    templateUrl: './two-factor-auth.component.html',
    styleUrls: ['./two-factor-auth.component.css']
})
export class TwoFactorAuthComponent {
    @Input() public login: string | undefined;
    loading = false;

    constructor(
        public activeModal: NgbActiveModal,
        private authService: AuthService
    ) {}

    onCodeCompleted(code: string) {
        this.loading = true;
        if (this.login != null) {
            this.authService
                .twoFactorAuthentication(this.login, code)
                .subscribe(() => {
                    this.activeModal.close(true);
                    this.loading = false;
                });
        }
    }
}
