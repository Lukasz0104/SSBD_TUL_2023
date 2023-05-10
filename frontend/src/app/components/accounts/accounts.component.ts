import { Component, OnInit } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { Account } from '../../model/account';
import { AccessType } from '../../model/access-type';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-accounts',
    templateUrl: './accounts.component.html'
})
export class AccountsComponent implements OnInit {
    accounts$: Observable<Account[]> | undefined;
    page = 1;
    pageSize = 3;

    chosenOption = new BehaviorSubject<number>(1);
    chosenAccessType = new BehaviorSubject<AccessType>(AccessType.OWNER);

    constructor(
        private accountService: AccountService,
        protected authService: AuthService
    ) {}

    ngOnInit() {
        if (this.authService.getCurrentGroup() === AccessType.ADMIN) {
            this.chosenAccessType.next(AccessType.ALL);
        }

        this.chosenOption.subscribe(() => {
            this.getAccounts();
        });

        this.chosenAccessType.subscribe(() => {
            this.getAccounts();
        });
    }

    getAccounts() {
        switch (this.chosenOption.getValue()) {
            case 1:
                this.accounts$ = this.accountService.getAccountsByTypeAndActive(
                    this.chosenAccessType.getValue(),
                    true
                );
                break;
            case 2:
                this.accounts$ = this.accountService.getAccountsByTypeAndActive(
                    this.chosenAccessType.getValue(),
                    false
                );
                break;
            case 3:
                this.accounts$ =
                    this.accountService.getUnapprovedAccountsByType(
                        this.chosenAccessType.getValue()
                    );
                break;
        }
    }

    changeStatusOption(option: number) {
        if (option != this.chosenOption.getValue()) {
            this.chosenOption.next(option);
        }
    }

    changeAccessTypeOption(option: string) {
        if (option != this.chosenAccessType.getValue()) {
            this.chosenAccessType.next(option as AccessType);
        }
    }

    reload() {
        this.getAccounts();
    }
}
