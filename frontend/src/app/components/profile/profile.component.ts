import { Component } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { OwnAccount } from '../../model/account';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css']
})
export class ProfileComponent {
    ownAccount$: Observable<OwnAccount>;

    constructor(accountService: AccountService) {
        this.ownAccount$ = accountService.getOwnProfilelogin();
    }
}
