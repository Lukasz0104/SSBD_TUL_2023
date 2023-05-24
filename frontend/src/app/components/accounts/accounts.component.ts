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
import { AccountPage } from '../../model/account-page';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
    selector: 'app-accounts',
    templateUrl: './accounts.component.html'
})
export class AccountsComponent implements OnInit {
    protected readonly accessTypeEnum = AccessType;

    accountsPage$: Observable<AccountPage> | undefined;
    page = 1;
    pageSize = 10;
    phrase = '';

    sortDirection = 1;

    chosenOption = new BehaviorSubject<number>(1);
    chosenAccessType = new BehaviorSubject<AccessType>(AccessType.OWNER);
    filter = new FormGroup({
        phrase: new FormControl('', {})
    });

    constructor(
        private accountService: AccountService,
        protected authService: AuthService,
        private modalService: NgbModal,
        private accessLevelService: AccessLevelService,
        private toastService: ToastService
    ) {}

    ngOnInit() {
        this.loadViewPreferences();

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
                this.accountsPage$ =
                    this.accountService.getAccountsByTypeAndActive(
                        this.chosenAccessType.getValue(),
                        true,
                        this.page - 1,
                        this.pageSize,
                        this.sortDirection === 1,
                        this.phrase
                    );
                break;
            case 2:
                this.accountsPage$ =
                    this.accountService.getAccountsByTypeAndActive(
                        this.chosenAccessType.getValue(),
                        false,
                        this.page - 1,
                        this.pageSize,
                        this.sortDirection === 1,
                        this.phrase
                    );
                break;
            case 3:
                this.accountsPage$ =
                    this.accountService.getUnapprovedAccountsByType(
                        this.chosenAccessType.getValue(),
                        this.page - 1,
                        this.pageSize,
                        this.sortDirection === 1,
                        this.phrase
                    );
                break;
        }
    }

    loadViewPreferences() {
        this.authService
            .getCurrentGroupObservable()
            .subscribe((currentGroup) => {
                const pageSize = parseInt(
                    localStorage.getItem('pageSize') ?? '10'
                );

                if (![5, 10, 15].includes(pageSize)) {
                    this.pageSize = 10;
                    localStorage.setItem('pageSize', '10');
                }
                this.pageSize = pageSize;
                this.sortDirection = parseInt(
                    localStorage.getItem('sortDirection') ?? '1'
                );
                this.pageSize = parseInt(
                    localStorage.getItem('pageSize') ?? '10'
                );

                const accessTypeFilter =
                    localStorage.getItem('accessTypeFilter') ?? 'ALL';
                if (
                    currentGroup == AccessType.MANAGER &&
                    (accessTypeFilter == AccessType.MANAGER ||
                        accessTypeFilter == AccessType.ADMIN)
                ) {
                    this.chosenAccessType.next(AccessType.ALL);
                    localStorage.setItem('accessTypeFilter', AccessType.ALL);
                } else {
                    this.chosenAccessType.next(accessTypeFilter as AccessType);
                }

                let activeFilter = parseInt(
                    localStorage.getItem('activeFilter') ?? '1'
                );

                if (
                    (currentGroup == AccessType.ADMIN &&
                        activeFilter == 3 &&
                        accessTypeFilter != AccessType.MANAGER) ||
                    (currentGroup == AccessType.MANAGER &&
                        activeFilter == 3 &&
                        accessTypeFilter != AccessType.OWNER)
                ) {
                    activeFilter = 1;
                }
                localStorage.setItem('activeFilter', activeFilter.toString());
                this.chosenOption.next(activeFilter);
            });
    }

    changeStatusOption(option: number) {
        if (option != this.chosenOption.getValue()) {
            this.chosenOption.next(option);
            localStorage.setItem('activeFilter', option.toString());
        }
    }

    changeAccessTypeOption(option: string) {
        if (option != this.chosenAccessType.getValue()) {
            this.chosenAccessType.next(option as AccessType);
            localStorage.setItem('accessTypeFilter', option as AccessType);
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

    protected onSortChange() {
        this.sortDirection = -this.sortDirection;
        localStorage.setItem('sortDirection', this.sortDirection.toString());

        this.reload();
    }

    savePageSizePreference() {
        this.reload();
        localStorage.setItem('pageSize', this.pageSize.toString());
    }

    protected onFilterPhrase() {
        this.phrase = this.filter.getRawValue().phrase ?? '';
        this.reload();
    }

    protected clearFilterPhrase() {
        if (this.filter.getRawValue().phrase != null) {
            this.phrase = '';
            this.filter.controls.phrase.reset();
            this.reload();
        }
    }
}
