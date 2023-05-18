import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { switchMap } from 'rxjs';
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
                    return this.accountService.confirmRegistration(token);
                })
            )
            .subscribe((response) => {
                this.loading = false;
                this.success = !response;
                this.response = response;
            });
    }
}
