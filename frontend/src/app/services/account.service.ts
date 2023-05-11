import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import {
    HttpClient,
    HttpErrorResponse,
    HttpHeaders
} from '@angular/common/http';
import { Account, EditPersonalData, OwnAccount } from '../model/account';
import { ToastService } from './toast.service';
import { ResponseMessage } from '../common/response-message.enum';
import { AccessType, ChangeAccessLevelDto } from '../model/access-type';
import { catchError, EMPTY, map, Observable, of, tap } from 'rxjs';
import { AuthService } from './auth.service';
import { ChooseAccessLevelComponent } from '../components/modals/choose-access-level/choose-access-level.component';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    ifMatch = '';

    constructor(
        private http: HttpClient,
        private toastService: ToastService,
        private authService: AuthService,
        private modalService: NgbModal
    ) {}

    getOwnProfile() {
        return this.http
            .get<OwnAccount>(`${environment.apiUrl}/accounts/me`, {
                observe: 'response'
            })
            .pipe(
                map((response) => {
                    this.ifMatch = response.headers.get('ETag') ?? '';
                    return response.body;
                })
            );
    }

    getProfile(id: number) {
        return this.http
            .get<Account>(`${environment.apiUrl}/accounts/${id}`, {
                observe: 'response'
            })
            .pipe(
                map((response) => {
                    this.ifMatch = response.headers.get('ETag') ?? '';
                    return response.body;
                })
            );
    }

    editOwnProfile(dto: EditPersonalData) {
        return this.http
            .put<OwnAccount>(`${environment.apiUrl}/accounts/me`, dto, {
                headers: new HttpHeaders({ 'If-Match': this.ifMatch }),
                observe: 'response'
            })
            .pipe(
                tap(() => {
                    this.toastService.showSuccess('SUCCESS!');
                }),
                map(() => true),
                catchError((err: HttpErrorResponse) => {
                    this.toastService.showDanger(err.error.message);
                    switch (err.error.message) {
                        case ResponseMessage.OPTIMISTIC_LOCK:
                            return of(true);
                        case ResponseMessage.SIGNATURE_MISMATCH:
                            return of(true);
                    }
                    return of(false);
                })
            );
    }

    editPersonalDataAsAdmin(dto: EditPersonalData) {
        return this.http
            .put<Account>(
                `${environment.apiUrl}/accounts/admin/edit-other`,
                dto,
                {
                    headers: new HttpHeaders({ 'If-Match': this.ifMatch }),
                    observe: 'response'
                }
            )
            .pipe(
                map((): boolean => {
                    this.toastService.showSuccess(
                        'toast.edit-account-as-admin-success'
                    );
                    return true;
                }),
                catchError((err: HttpErrorResponse) => {
                    this.toastService.showDanger(err.error.message);
                    switch (err.error.message) {
                        case ResponseMessage.OPTIMISTIC_LOCK:
                            return of(true);
                        case ResponseMessage.SIGNATURE_MISMATCH:
                            return of(true);
                    }
                    return of(false);
                })
            );
    }

    getAccountsByTypeAndActive(type: AccessType, active: boolean) {
        let url;
        switch (type) {
            case AccessType.OWNER:
                url = 'accounts/owners';
                break;
            case AccessType.MANAGER:
                url = 'accounts/managers';
                break;
            case AccessType.ADMIN:
                url = 'accounts/admins';
                break;
            default:
                url = 'accounts';
                break;
        }
        return this.http.get<Account[]>(
            `${environment.apiUrl}/${url}?active=${active}`
        );
    }

    getUnapprovedAccountsByType(type: AccessType) {
        if (type == AccessType.OWNER) {
            return this.http.get<Account[]>(
                `${environment.apiUrl}/accounts/owners/unapproved`
            );
        } else if (type == AccessType.MANAGER) {
            return this.http.get<Account[]>(
                `${environment.apiUrl}/accounts/managers/unapproved`
            );
        }
        return of([]);
    }

    changePassword(dto: object): Observable<boolean> {
        return this.http
            .put(`${environment.apiUrl}/accounts/me/change-password`, dto)
            .pipe(
                map(() => {
                    this.toastService.showSuccess(
                        'toast.change-password-success'
                    );
                    return true;
                }),
                catchError((res: HttpErrorResponse) => {
                    this.toastService.showDanger(`toast.${res.error.message}`);
                    return of(false);
                })
            );
    }

    changeAccessLevel(): void {
        const groups: AccessType[] = this.authService
            .getGroups()
            .filter((x) => x !== this.authService.getCurrentGroup());
        if (groups.length < 1) {
            return;
        }
        const modalRef: NgbModalRef = this.modalService.open(
            ChooseAccessLevelComponent,
            { centered: true }
        );

        modalRef.componentInstance.groups = groups;
        modalRef.result
            .then((grp: AccessType): void => {
                this.http
                    .put<ChangeAccessLevelDto>(
                        `${environment.apiUrl}/accounts/me/change-access-level`,
                        { accessType: grp }
                    )
                    .pipe(
                        map((res: ChangeAccessLevelDto): void => {
                            this.authService.setCurrentGroup(res.accessType);
                            this.toastService.showSuccess(
                                'toast.switch-access-level-success'
                            );
                        }),
                        catchError(() => {
                            this.toastService.showDanger(
                                'toast.switch-access-level-fail'
                            );
                            this.authService.logout();
                            return EMPTY;
                        })
                    )
                    .subscribe();
            })
            .catch(() => false);
    }
}
