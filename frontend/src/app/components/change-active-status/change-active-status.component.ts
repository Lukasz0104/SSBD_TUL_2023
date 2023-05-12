import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { ToastService } from '../../services/toast.service';
import { AuthService } from '../../services/auth.service';
import { AccessType } from '../../model/access-type';

@Component({
    selector: 'app-change-active-status',
    templateUrl: './change-active-status.component.html'
})
export class ChangeActiveStatusComponent implements OnInit {
    loading = false;
    @Input() id: number | undefined;
    @Input() status: boolean | undefined;
    @Output() statusChangedEvent = new EventEmitter<null>();
    private currentAccessLevel: AccessType | undefined;

    constructor(
        private statusService: AccountService,
        private toastService: ToastService,
        private authService: AuthService
    ) {}

    ngOnInit(): void {
        this.currentAccessLevel = this.authService.getCurrentGroup();
    }

    onClick() {
        const id = this.id;
        const status = !this.status; //Notice the exclamation mark!
        if (id === undefined || status === undefined) {
            this.toastService.showDanger('something.went.wrong');
        } else {
            this.loading = true;
            if (this.currentAccessLevel == AccessType.ADMIN) {
                this.statusService
                    .changeActiveStatusAsAdmin(id, status)
                    .subscribe((result: boolean) => {
                        this.loading = false;
                        if (result) {
                            this.statusChangedEvent.emit(); //Emit to refresh accounts list
                        }
                    });
            } else if (this.currentAccessLevel == AccessType.MANAGER) {
                this.statusService
                    .changeActiveStatusAsManager(id, status)
                    .subscribe((result: boolean) => {
                        this.loading = false;
                        if (result) {
                            this.statusChangedEvent.emit(); //Emit to refresh accounts list
                        }
                    });
            } else {
                this.toastService.showDanger('something.went.wrong'); // ... ?
            }
        }
    }
}
