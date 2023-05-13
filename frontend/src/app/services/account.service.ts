import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import {
    HttpClient,
    HttpErrorResponse,
    HttpHeaders
} from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, EMPTY, map, Observable, of, tap } from 'rxjs';
import { Account, EditPersonalData, OwnAccount } from '../model/account';
import { ToastService } from './toast.service';
import { ResponseMessage } from '../common/response-message.enum';
import { AccessType, ChangeAccessLevelDto } from '../model/access-type';
import { AuthService } from './auth.service';
import { ChooseAccessLevelComponent } from '../components/modals/choose-access-level/choose-access-level.component';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { ChangeEmailForm } from '../model/email-form';
import { ActiveStatusDto } from '../model/active-status-dto';
import {
    RegisterManagerDto,
    RegisterOwnerDto
} from '../model/registration.dto';

type RegisterResponse = { message: ResponseMessage };

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    ifMatch = '';
    private readonly accountsUrl = `${environment.apiUrl}/accounts`;

    constructor(
        private http: HttpClient,
        private router: Router,
        private toastService: ToastService,
        private authService: AuthService,
        private modalService: NgbModal
    ) {}

    resetPassword(email: string) {
        return this.http
            .post(
                `${this.accountsUrl}/reset-password-message?email=` + email,
                {}
            )
            .pipe(
                map(() => {
                    this.router.navigate(['/login']).then(() => {
                        this.toastService.clearAll();
                        this.toastService.showSuccess(
                            'toast.account.reset-password-message'
                        );
                    });
                }),
                catchError((response: HttpErrorResponse) => {
                    if (response.status == 404) {
                        this.router.navigate(['/login']).then(() => {
                            this.toastService.clearAll();
                            this.toastService.showSuccess(
                                'toast.account.reset-password-message'
                            );
                        });
                    } else {
                        this.toastService.showDanger(
                            'toast.account.reset-password-message-fail'
                        );
                    }
                    return of(null);
                })
            );
    }

    changeLanguage(language: string) {
        return this.http
            .put(`${this.accountsUrl}/me/change-language/` + language, {})
            .pipe(
                map(() => {
                    return true;
                }),
                catchError(() => of(false))
            );
    }

    resetPasswordConfirm(resetPasswordDTO: object) {
        return this.http
            .post(`${this.accountsUrl}/reset-password`, resetPasswordDTO)
            .pipe(
                map(() => {
                    this.router.navigate(['/login']).then(() => {
                        this.toastService.clearAll();
                        this.toastService.showSuccess(
                            'toast.account.reset-password-change'
                        );
                    });
                }),
                catchError((response: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.reset-password-fail',
                        'password-change',
                        response
                    );
                    this.router.navigate(['/']);
                    return EMPTY;
                })
            )
            .subscribe();
    }

    forcePasswordChange(login: string) {
        return this.http
            .put(`${this.accountsUrl}/force-password-change/` + login, {})
            .pipe(
                map(() => {
                    this.toastService.clearAll();
                    this.toastService.showSuccess(
                        'toast.account.force-password-change-message'
                    );
                }),
                catchError((response: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.reset-password-fail',
                        'force-password-change',
                        response
                    );
                    return EMPTY;
                })
            )
            .subscribe();
    }

    overrideForcePasswordChange(resetPasswordDTO: object) {
        return this.http
            .put(
                `${this.accountsUrl}/override-forced-password`,
                resetPasswordDTO
            )
            .pipe(
                map(() => {
                    this.router.navigate(['/login']).then(() => {
                        this.toastService.clearAll();
                        this.toastService.showSuccess(
                            'toast.account.force-password-change-override-message'
                        );
                    });
                }),
                catchError((response: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.reset-password-fail',
                        'password-change',
                        response
                    );
                    this.router.navigate(['/']);
                    return EMPTY;
                })
            )
            .subscribe();
    }

    register(
        dto: RegisterOwnerDto | RegisterManagerDto
    ): Observable<ResponseMessage | null> {
        const isManagerDto = 'licenseNumber' in dto && !!dto.licenseNumber;

        return this.http
            .post<RegisterResponse | null>(
                `${this.accountsUrl}/register/${
                    isManagerDto ? 'manager' : 'owner'
                }`,
                dto
            )
            .pipe(
                map((res) => res?.message ?? null),
                catchError((e: HttpErrorResponse) => of(e.error.message))
            );
    }

    confirmRegistration(token: string): Observable<ResponseMessage | null> {
        return this.http
            .post<RegisterResponse | null>(
                `${this.accountsUrl}/confirm-registration`,
                null,
                {
                    params: { token }
                }
            )
            .pipe(
                map((res) => res?.message ?? null),
                catchError((e: HttpErrorResponse) => of(e.error.message))
            );
    }

    getOwnProfile() {
        return this.http
            .get<OwnAccount>(`${this.accountsUrl}/me`, {
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
            .get<Account>(`${this.accountsUrl}/${id}`, {
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
            .put<OwnAccount>(`${this.accountsUrl}/me`, dto, {
                headers: new HttpHeaders({ 'If-Match': this.ifMatch }),
                observe: 'response'
            })
            .pipe(
                tap(() => {
                    this.toastService.showSuccess('SUCCESS!');
                }),
                map(() => true),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.edit-own-account-fail',
                        'edit-own-profile',
                        err
                    );
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
            .put<Account>(`${this.accountsUrl}/admin/edit-other`, dto, {
                headers: new HttpHeaders({ 'If-Match': this.ifMatch }),
                observe: 'response'
            })
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
                `${this.accountsUrl}/owners/unapproved`
            );
        } else if (type == AccessType.MANAGER) {
            return this.http.get<Account[]>(
                `${this.accountsUrl}/managers/unapproved`
            );
        }
        return of([]);
    }

    handleError(
        genericMessageKey: string,
        method: string,
        response: HttpErrorResponse
    ) {
        if (response.status == 500 || response.error.message == null) {
            this.toastService.showDanger(genericMessageKey);
        } else {
            this.toastService.showDanger(method + '.' + response.error.message);
        }
    }

    changeEmail() {
        return this.http.post(`${this.accountsUrl}/me/change-email`, null).pipe(
            map(() => {
                this.toastService.showSuccess('mail.sent.success'); //fixme
                return true;
            }),
            catchError((err: HttpErrorResponse) => {
                this.toastService.showDanger(err.error.message);
                return of(false);
            })
        );
    }

    confirmEmail(email: string, token: string) {
        return this.http
            .put<ChangeEmailForm>(
                `${this.accountsUrl}/me/confirm-email/${token}`,
                { email }
            )
            .pipe(
                map(() => {
                    this.toastService.showSuccess('email.change.success');
                    return true;
                }),
                catchError((err: HttpErrorResponse) => {
                    this.toastService.showDanger(err.error.message);
                    return of(false);
                })
            );
    }

    changeActiveStatusAsManager(id: number, active: boolean) {
        return this.http
            .put<ActiveStatusDto>(
                `${this.accountsUrl}/manager/change-active-status`,
                {
                    id,
                    active
                }
            )
            .pipe(
                map(() => {
                    this.toastService.showSuccess('status.change.success'); //todo
                    return true;
                }),
                catchError((err: HttpErrorResponse) => {
                    this.toastService.showDanger(err.error.message);
                    return of(false);
                })
            );
    }

    changeActiveStatusAsAdmin(id: number, active: boolean) {
        return this.http
            .put<ActiveStatusDto>(
                `${this.accountsUrl}/admin/change-active-status`,
                {
                    id,
                    active
                }
            )
            .pipe(
                map(() => {
                    this.toastService.showSuccess('status.change.success'); //todo
                    return true;
                }),
                catchError((err: HttpErrorResponse) => {
                    this.toastService.showDanger(err.error.message); // TODO add translation for response.message.bad-access-level
                    return of(false);
                })
            );
    }

    changePassword(dto: object): Observable<boolean> {
        return this.http
            .put(`${this.accountsUrl}/me/change-password`, dto)
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
                        `${this.accountsUrl}/me/change-access-level`,
                        { accessType: grp }
                    )
                    .pipe(
                        map((res: ChangeAccessLevelDto): void => {
                            this.authService.setCurrentGroup(res.accessType);
                            this.toastService.showSuccess(
                                'toast.switch-access-level-success'
                            );
                            this.router.navigate([this.router.url]);
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
