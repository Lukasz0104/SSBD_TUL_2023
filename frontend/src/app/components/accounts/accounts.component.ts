import { Component, OnInit } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { Account } from '../../model/account';

@Component({
    selector: 'app-accounts',
    templateUrl: './accounts.component.html'
})
export class AccountsComponent implements OnInit {
    accounts$: Observable<Account[]> | undefined;
    page = 1;
    pageSize = 3;

    chosenOption = new BehaviorSubject<number>(1);

    constructor(private accountService: AccountService) {
        this.chosenOption.subscribe(() => {
            this.getAccounts();
        });
    }

    getAccounts() {
        switch (this.chosenOption.getValue()) {
            case 1:
                this.accounts$ = this.accountService.getOwners();
                break;
            case 2:
                this.accounts$ = this.accountService.getOwnersByActive(true);
                break;
            case 3:
                this.accounts$ = this.accountService.getOwnersByActive(false);
                break;
            case 4:
                this.accounts$ = this.accountService.getOwners();
                break;
        }
    }

    ngOnInit(): void {
        this.getAccounts();
    }

    changeOption(option: number) {
        if (option != this.chosenOption.getValue()) {
            this.chosenOption.next(option);
        }
    }

    reload() {
        this.chosenOption.next(this.chosenOption.getValue());
    }
}
