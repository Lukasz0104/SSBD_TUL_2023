import { Component, OnInit } from '@angular/core';
import { catchError, EMPTY, map, Observable } from 'rxjs';
import { Account } from '../../model/account';
import { AccountService } from '../../services/account.service';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EditPersonalDataAsAdminComponent } from '../modals/edit-personal-data-as-admin/edit-personal-data-as-admin.component';

@Component({
    selector: 'app-account',
    templateUrl: './account.component.html'
})
export class AccountComponent implements OnInit {
    id: number | undefined;
    account$: Observable<Account | null> | undefined;

    constructor(
        private accountService: AccountService,
        private route: ActivatedRoute,
        private modalService: NgbModal
    ) {}

    ngOnInit() {
        this.route.queryParams.subscribe((params) => {
            this.id = params['id'];
            if (this.id !== undefined) {
                this.account$ = this.accountService.getProfile(this.id);
            }
        });
    }

    editPersonalDataAsAdmin(): void {
        this.account$
            ?.pipe(
                map((account: Account | null) => {
                    if (account) {
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
                            .then((): void => {
                                this.account$ = this.accountService.getProfile(
                                    account.id
                                );
                            })
                            .catch(() => {
                                this.modalService.dismissAll();
                                return EMPTY;
                            });
                    }
                }),
                catchError(() => {
                    this.modalService.dismissAll();
                    return EMPTY;
                })
            )
            .subscribe();
    }
}
