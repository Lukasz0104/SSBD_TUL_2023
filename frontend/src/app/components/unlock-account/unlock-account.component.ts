import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AccountService } from '../../services/account.service';

@Component({
    selector: 'app-unlock-account',
    templateUrl: './unlock-account.component.html'
})
export class UnlockAccountComponent implements OnInit {
    protected loading = true;
    protected success = false;
    protected response: string | null = null;

    constructor(
        private accountService: AccountService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit(): void {
        const token = this.activatedRoute.snapshot.queryParams['token'];

        if (!token) {
            this.response = 'response.message.no-token';
            this.success = false;
            this.loading = false;
        } else {
            this.accountService.unlockAccount(token).subscribe((response) => {
                this.loading = false;
                this.success = !response;
                if (response.includes('invalid.uuid')) {
                    this.response = 'response.message.token_not_found';
                } else {
                    this.response = response;
                }
            });
        }
    }
}
