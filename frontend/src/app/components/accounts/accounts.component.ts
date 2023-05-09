import { Component, OnInit } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { Observable } from 'rxjs';
import { Account } from '../../model/account';

@Component({
    selector: 'app-accounts',
    templateUrl: './accounts.component.html'
})
export class AccountsComponent implements OnInit {
    accounts$: Observable<Account[]> | undefined;
    page = 1;
    pageSize = 3;

    constructor(private accountService: AccountService) {
        this.getAccounts();
    }

    getAccounts() {
        this.accounts$ = this.accountService.getOwners();
    }

    ngOnInit(): void {
        this.getAccounts();
    }
}
