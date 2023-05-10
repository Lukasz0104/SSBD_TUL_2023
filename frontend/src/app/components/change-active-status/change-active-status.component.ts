import { Component, OnInit } from '@angular/core';
import { ChangeActiveStatusService } from '../../services/change-active-status.service';
import { ToastService } from '../../services/toast.service';
import { AuthService } from '../../services/auth.service';
import { AccessLevel } from '../../model/access-level';

@Component({
    selector: 'app-change-active-status',
    templateUrl: './change-active-status.component.html'
})
export class ChangeActiveStatusComponent implements OnInit {
    private currentAccessLevel: AccessLevel | undefined;
    private id: number; //todo get status from user account

    constructor(
        private statusService: ChangeActiveStatusService,
        private toastService: ToastService,
        private authService: AuthService
    ) {
        this.id = -5; //fixme
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
        let result = false;

        if (this.currentAccessLevel == AccessLevel.ADMIN) {
            this.statusService
                .changeActiveStatusAsAdmin(id, status)
                .subscribe((HttpResult: boolean) => {
                    result = HttpResult;
                });
        } else if (this.currentAccessLevel == AccessLevel.MANAGER) {
            this.statusService
                .changeActiveStatusAsManager(id, status)
                .subscribe((HttpResult: boolean) => {
                    result = HttpResult;
                });
        } else {
            result = false;
        }

        if (result) {
            this.toastService.showSuccess('status.changed.success');
        } else {
            this.toastService.showDanger('status.changed.failure');
        }
    }
}
