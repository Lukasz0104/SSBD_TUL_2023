import { Component, OnInit } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { BehaviorSubject, EMPTY, filter, Observable, switchMap } from 'rxjs';
import { AccessType } from '../../model/access-type';
import { Account } from '../../model/account';
import { AccessLevelService } from '../../services/access-level.service';
import { AccountService } from '../../services/account.service';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../services/toast.service';
import { ConfirmActionComponent } from '../modals/confirm-action/confirm-action.component';
import { EditPersonalDataAsAdminComponent } from '../modals/edit-personal-data-as-admin/edit-personal-data-as-admin.component';
import { GrantAccessLevelComponent } from '../modals/grant-access-level/grant-access-level.component';

@Component({
    selector: 'app-accounts',
    templateUrl: './accounts.component.html'
})
export class AccountsComponent implements OnInit {
    protected readonly accessTypeEnum = AccessType;

    accounts$: Observable<Account[]> | undefined;
    page = 1;
    pageSize = 10;

    chosenOption = new BehaviorSubject<number>(1);
    chosenAccessType = new BehaviorSubject<AccessType>(AccessType.OWNER);

    constructor(
        private accountService: AccountService,
        protected authService: AuthService,
        private modalService: NgbModal,
        private accessLevelService: AccessLevelService,
        private toastService: ToastService
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

    forceChange(login: string) {
        const modalRef = this.modalService.open(ConfirmActionComponent, {
            centered: true
        });
        modalRef.componentInstance.message =
            'modal.confirm-action.force-password-message';
        modalRef.componentInstance.danger =
            'modal.confirm-action.force-password-danger';
        modalRef.result.then((result: boolean) => {
            if (result) {
                this.accountService.forcePasswordChange(login).subscribe(() => {
                    this.reload();
                });
            }
        });
    }

    editPersonalDataAsAdmin(account: Account): void {
        const modalRef: NgbModalRef = this.modalService.open(
            EditPersonalDataAsAdminComponent,
            {
                centered: true,
                size: 'xl',
                scrollable: true
            }
        );
        modalRef.componentInstance.setAccount(account);
        modalRef.result
            .then((res): void => {
                account = res;
            })
            .catch(() => EMPTY);
        this.reload();
    }

    openGrantAccessLevelModal(
        account: Account,
        level: AccessType,
        grantNewAccessLevelMode = true
    ) {
        const modal = this.modalService.open(GrantAccessLevelComponent);
        const instance = modal.componentInstance as GrantAccessLevelComponent;

        instance.accessType = level;
        instance.id = account.id;
        instance.grantNewAccessLevelMode = grantNewAccessLevelMode;
        instance.accessLevel = account.accessLevels.filter(
            (l) => l.level === level
        )[0];

        modal.closed.subscribe(() => this.reload());
    }

    openRevokeAccessLevelModal(account: Account, level: AccessType) {
        const modal = this.modalService.open(ConfirmActionComponent);
        const instance = modal.componentInstance as ConfirmActionComponent;

        instance.message = 'modal.confirm-action.revoke-access-level';
        instance.danger = `access-levels.${level}`;

        modal.closed
            .pipe(
                filter((flag) => flag),
                switchMap(() =>
                    this.accessLevelService.reject(account.id, level)
                )
            )
            .subscribe((res) => {
                if (res) {
                    this.toastService.showSuccess(
                        'toast.account.revoke-access-level'
                    );
                } else {
                    this.toastService.showDanger(
                        'toast.account.revoke-access-level-fail'
                    );
                }
                this.reload();
            });
    }

    protected hasAccessLevel(account: Account, level: AccessType) {
        return (
            account.accessLevels.filter((al) => al.level === level && al.active)
                .length === 1
        );
    }

    protected showGrantAdminDropdownItem(account: Account): boolean {
        const chosen = this.chosenAccessType.getValue();
        return (
            this.authService.getCurrentGroup() === AccessType.ADMIN &&
            chosen === AccessType.ALL &&
            !this.hasAccessLevel(account, AccessType.ADMIN)
        );
    }

    protected showGrantManagerDropdownItem(account: Account): boolean {
        const chosen = this.chosenAccessType.getValue();
        return (
            this.authService.getCurrentGroup() === AccessType.ADMIN &&
            chosen === AccessType.ALL &&
            !this.hasAccessLevel(account, AccessType.MANAGER)
        );
    }

    protected showGrantOwnerDropdownItem(account: Account): boolean {
        const chosen = this.chosenAccessType.getValue();
        return (
            this.authService.getCurrentGroup() === AccessType.MANAGER &&
            chosen === AccessType.ALL &&
            !this.hasAccessLevel(account, AccessType.OWNER)
        );
    }

    protected showRevokeAdminDropdownItem(account: Account): boolean {
        const chosen = this.chosenAccessType.getValue();
        return (
            this.authService.getCurrentGroup() === AccessType.ADMIN &&
            (chosen === AccessType.ALL || chosen === AccessType.ADMIN) &&
            this.hasAccessLevel(account, AccessType.ADMIN)
        );
    }

    protected showRevokeManagerDropdownItem(account: Account): boolean {
        const chosen = this.chosenAccessType.getValue();
        return (
            this.authService.getCurrentGroup() === AccessType.ADMIN &&
            (chosen === AccessType.ALL || chosen === AccessType.MANAGER) &&
            this.hasAccessLevel(account, AccessType.MANAGER)
        );
    }

    protected showRevokeOwnerDropdownItem(account: Account): boolean {
        const chosen = this.chosenAccessType.getValue();
        return (
            this.authService.getCurrentGroup() === AccessType.MANAGER &&
            (chosen === AccessType.ALL || chosen === AccessType.OWNER) &&
            this.hasAccessLevel(account, AccessType.OWNER)
        );
    }
}
