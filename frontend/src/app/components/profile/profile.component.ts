import { Component } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { OwnAccount } from '../../model/account';
import { Observable } from 'rxjs';
import { EditPersonalDataComponent } from '../modals/edit-personal-data/edit-personal-data.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html'
})
export class ProfileComponent {
    ownAccount$: Observable<OwnAccount | null>;

    constructor(
        private accountService: AccountService,
        private modalService: NgbModal
    ) {
        this.ownAccount$ = accountService.getOwnProfile();
    }

    edit() {
        const modalRef = this.modalService.open(EditPersonalDataComponent, {
            centered: true,
            size: 'xl',
            scrollable: true
        });
        modalRef.result.then(() => {
            this.ownAccount$ = this.accountService.getOwnProfile();
        });
    }

    changeTwoFactorAuthStatus() {
        this.ownAccount$.subscribe((result) => {
            if (result != null) {
                this.accountService.changeTwoFactorAuthStatus(
                    !result.twoFactorAuth
                );
                result.twoFactorAuth = !result.twoFactorAuth;
            }
        });
    }
}
