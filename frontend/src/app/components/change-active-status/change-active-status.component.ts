import { Component, OnInit } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { ToastService } from '../../services/toast.service';
import { AuthService } from '../../services/auth.service';
import { AccessLevel } from '../../model/access-level';

@Component({
    selector: 'app-change-active-status',
    templateUrl: './change-active-status.component.html'
})
export class ChangeActiveStatusComponent implements OnInit {
    loading = false;
    private currentAccessLevel: AccessLevel | undefined;
    private id: number; //todo get status from user account

    constructor(
        private statusService: AccountService,
        private toastService: ToastService,
        private authService: AuthService
    ) {
        this.id = -3; //fixme
        this._status = true; //fixme
    }

    private _status: boolean; //todo get status from user account

    get status(): boolean {
        return <boolean>this._status;
    }

    ngOnInit(): void {
        this.currentAccessLevel = this.authService.getCurrentGroup();
    }

    onClick() {
        const id = this.id;
        const status = this._status;
        this.loading = true;

        if (this.currentAccessLevel == AccessLevel.ADMIN) {
            this.statusService
                .changeActiveStatusAsAdmin(id, status)
                .subscribe((HttpResult: boolean) => {
                    this.loading = false;
                    if (!HttpResult) {
                        this.toastService.showDanger('status.changed.failure');
                    } else {
                        this.toastService.showSuccess('status.changed.success');
                    }
                });
        } else if (this.currentAccessLevel == AccessLevel.MANAGER) {
            this.statusService
                .changeActiveStatusAsManager(id, status)
                .subscribe((HttpResult: boolean) => {
                    this.loading = false;
                    if (!HttpResult) {
                        this.toastService.showDanger('status.changed.failure');
                    } else {
                        this.toastService.showSuccess('status.changed.success');
                    }
                });
        } else {
            this.toastService.showDanger('status.changed.failure');
        }
    }
}
