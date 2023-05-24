import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { of, switchMap } from 'rxjs';
import { AccountService } from '../../services/account.service';

@Component({
    selector: 'app-confirm-registration',
    templateUrl: './confirm-registration.component.html'
})
export class ConfirmRegistrationComponent implements OnInit {
    protected loading = true;
    protected success = false;
    protected response: string | null = null;

    constructor(
        private accountService: AccountService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit(): void {
        this.activatedRoute.queryParams
            .pipe(
                switchMap((p) => {
                    const token = p['token'];
                    if (token) {
                        return this.accountService.confirmRegistration(token);
                    }
                    return of('response.message.no-token');
                })
            )
            .subscribe((response) => {
                this.loading = false;
                this.success = !response;
                this.response = response;
            });
    }
}
