import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Account } from '../../model/account';
import { AccountService } from '../../services/account.service';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-account',
    templateUrl: './account.component.html'
})
export class AccountComponent implements OnInit {
    id: number | undefined;
    account$: Observable<Account> | undefined;

    constructor(
        private accountService: AccountService,
        private route: ActivatedRoute
    ) {}

    ngOnInit() {
        this.route.queryParams.subscribe((params) => {
            this.id = params['id'];
            if (this.id !== undefined) {
                this.account$ = this.accountService.getProfile(this.id);
            }
        });
    }
}
