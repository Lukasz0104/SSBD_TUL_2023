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
import { AccountPage } from '../model/account-page';

type RegisterResponse = { message: ResponseMessage };

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    ifMatch = '';
    private readonly accountsUrl = `${environment.apiUrl}/accounts`;
    private readonly cityDictUrl = `${environment.apiUrl}/city-dict`;

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
                catchError(() => {
                    this.toastService.showDanger(
                        'toast.account.reset-password-message-fail'
                    );
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
        this.http
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
                    return of(null);
                })
            );
    }

    overrideForcePasswordChange(resetPasswordDTO: object) {
        this.http
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

    register(dto: RegisterOwnerDto | RegisterManagerDto) {
        const isManagerDto = 'licenseNumber' in dto && !!dto.licenseNumber;

        return this.http
            .post<RegisterResponse | null>(
                `${this.accountsUrl}/register/${
                    isManagerDto ? 'manager' : 'owner'
                }`,
                dto
            )
            .pipe(
                map(() => of(true)),
                catchError((e: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.register-fail',
                        'register',
                        e
                    );
                    return EMPTY;
                })
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
                catchError((e: HttpErrorResponse) => {
                    if (e.error.message == null || e.status == 500) {
                        this.toastService.showDanger(
                            'toast.account.confirm-register-fail'
                        );
                    }
                    return of(e.error.message);
                })
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
                    this.toastService.showSuccess(
                        'toast.account.edit-own-account'
                    );
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
                        'toast.account.edit-account-as-admin'
                    );
                    return true;
                }),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.edit-account-as-admin-fail',
                        'edit-as-admin',
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

    getAccountsByTypeAndActive(
        type: AccessType,
        active: boolean,
        page: number,
        size: number,
        sortDirection: boolean,
        phrase: string,
        login: string
    ) {
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
        return this.http.get<AccountPage>(
            `${environment.apiUrl}/${url}?active=${active}&page=${page}&pageSize=${size}&asc=${sortDirection}&phrase=${phrase}&login=${login}`
        );
    }

    getUnapprovedAccountsByType(
        type: AccessType,
        page: number,
        size: number,
        sortDirection: boolean,
        phrase: string,
        login: string
    ) {
        if (type == AccessType.OWNER) {
            return this.http.get<AccountPage>(
                `${this.accountsUrl}/owners/unapproved?page=${page}&pageSize=${size}&asc=${sortDirection}&phrase=${phrase}&login=${login}`
            );
        } else if (type == AccessType.MANAGER) {
            return this.http.get<AccountPage>(
                `${this.accountsUrl}/managers/unapproved?page=${page}&pageSize=${size}&asc=${sortDirection}&phrase=${phrase}&login=${login}`
            );
        }
        return EMPTY;
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
                this.toastService.showSuccess('toast.account.change-email');
                return true;
            }),
            catchError((err: HttpErrorResponse) => {
                this.handleError(
                    'toast.account.change-email-fail',
                    'change-email',
                    err
                );
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
                    this.toastService.showSuccess(
                        'toast.account.change-email-confirm'
                    );
                    return true;
                }),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.change-email-confirm-fail',
                        'change-email-confirm',
                        err
                    );
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
                    this.toastService.showSuccess(
                        'toast.account.change-status'
                    );
                    return true;
                }),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.change-status-fail',
                        'change-status',
                        err
                    );
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
                    this.toastService.showSuccess(
                        'toast.account.change-status'
                    );
                    return true;
                }),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.change-status-fail',
                        'change-status',
                        err
                    );
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
                        'toast.account.change-password'
                    );
                    return true;
                }),
                catchError((res: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.change-password-fail',
                        'change-password',
                        res
                    );
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
                                'toast.account.change-access-level'
                            );
                            this.router.navigate([this.router.url]);
                        }),
                        catchError((err: HttpErrorResponse) => {
                            this.handleError(
                                'toast.account.change-access-level-fail',
                                'change-access-level',
                                err
                            );
                            this.authService.logout();
                            return EMPTY;
                        })
                    )
                    .subscribe();
            })
            .catch(() => false);
    }

    changeTwoFactorAuthStatus(status: boolean) {
        this.http
            .put(
                `${this.accountsUrl}/me/change_two_factor_auth_status?status=` +
                    status,
                {}
            )
            .pipe(
                map(() => {
                    this.toastService.clearAll();
                    if (status) {
                        this.toastService.showSuccess(
                            'toast.account.two-factor-status-active'
                        );
                    } else {
                        this.toastService.showSuccess(
                            'toast.account.two-factor-status-inactive'
                        );
                    }
                }),
                catchError((response: HttpErrorResponse) => {
                    this.handleError(
                        'toast.account.two-factor-status-fail',
                        'change-two-factor-status',
                        response
                    );
                    return EMPTY;
                })
            )
            .subscribe();
    }

    updateThemePreferrence(isLightPreferred: boolean) {
        if (this.authService.isAuthenticated()) {
            this.http
                .put(`${this.accountsUrl}/me/theme`, null, {
                    params: {
                        light: isLightPreferred
                    }
                })
                .subscribe();
        }
    }
    getCitiesByPattern(cityPattern: string) {
        return this.http.get<readonly string[]>(
            `${this.cityDictUrl}/city?pattern=${cityPattern}`
        );
    }

    getLogins(input: string) {
        return this.http.get<string[]>(
            `${this.accountsUrl}/logins?login=` + input
        );
    }
}
