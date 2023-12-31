import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import jwtDecode from 'jwt-decode';
import { BehaviorSubject, catchError, EMPTY, map, of, tap } from 'rxjs';
import { ChooseAccessLevelComponent } from '../components/choose-access-level/choose-access-level.component';
import { RefreshSessionComponent } from '../components/refresh-session/refresh-session.component';
import { TwoFactorAuthComponent } from '../components/two-factor-auth/two-factor-auth.component';
import { AccessLevels, AccessType } from '../model/access-type';
import { LoginResponse } from '../model/login-response';
import { AppConfigService } from './app-config.service';
import { ToastService } from './toast.service';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private authenticated = new BehaviorSubject<boolean>(false);
    private currentGroup = new BehaviorSubject<AccessType>(AccessLevels.NONE);
    private scheduledRefresh: any;

    constructor(
        private http: HttpClient,
        private modalService: NgbModal,
        private router: Router,
        private toastService: ToastService,
        private translate: TranslateService,
        private appConfig: AppConfigService
    ) {
        this.handleLocalStorageContent();
        this.addLocalStorageListener();
    }

    handleLocalStorageContent() {
        const jwt = localStorage.getItem('jwt');
        if (jwt) {
            const decodedJwtToken = this.getDecodedJwtToken(jwt);
            if (decodedJwtToken) {
                if (decodedJwtToken.exp * 1000 < Date.now()) {
                    window.location.href = '/login';
                    this.clearUserData();
                }
                const group = localStorage.getItem('currentGroup');
                if (
                    group == AccessLevels.ADMIN ||
                    group == AccessLevels.MANAGER ||
                    group == AccessLevels.OWNER
                ) {
                    this.authenticated.next(true);
                    this.currentGroup.next(group as AccessType);
                    this.scheduleRefreshSessionPopUp();
                } else {
                    window.location.href = '/login';
                    this.clearUserData();
                }
            }
        }
    }

    addLocalStorageListener() {
        window.addEventListener(
            'storage',
            (event) => {
                if (event.storageArea == localStorage) {
                    const token = this.getJwt();
                    if (token == undefined) {
                        window.location.href = '/login';
                        this.clearUserData();
                    }
                }
            },
            false
        );
    }

    login(login: string, password: string) {
        return this.http
            .post<LoginResponse>(
                `${this.appConfig.apiUrl}/login`,
                {
                    login,
                    password
                },
                { observe: 'response' }
            )
            .pipe(
                map((response) => {
                    if (response.status == 202) {
                        this.handleTwoFactorAuthentication(login);
                        return true;
                    }
                    if (response.body != null) {
                        this.handleMultipleAccessLevels(response.body);
                        return true;
                    }
                    return false;
                }),
                catchError(() => of(false))
            );
    }

    handleMultipleAccessLevels(userData: LoginResponse) {
        const groups = this.getGroupsFromJwt(userData.jwt);
        if (groups.length === 1) {
            this.loginSuccessfulHandler(userData, groups[0], true);
        } else {
            const modalRef = this.modalService.open(
                ChooseAccessLevelComponent,
                {
                    centered: true
                }
            );
            modalRef.componentInstance.groups = groups;
            modalRef.result
                .then((choice) => {
                    this.loginSuccessfulHandler(userData, choice, true);
                })
                .catch(() => {
                    this.loginSuccessfulHandler(userData, groups[0], true);
                });
        }
    }

    private handleTwoFactorAuthentication(login: string) {
        const modalRef = this.modalService.open(TwoFactorAuthComponent, {
            backdrop: 'static',
            centered: true
        });
        modalRef.componentInstance.login = login;
        modalRef.result.then((result) => {
            if (!result) {
                this.toastService.showDanger('toast.auth.unsuccessful-login');
            }
        });
    }

    twoFactorAuthentication(login: string, code: string) {
        return this.http
            .post<LoginResponse>(`${this.appConfig.apiUrl}/confirm-login`, {
                login,
                code
            })
            .pipe(
                map((response) => {
                    this.handleMultipleAccessLevels(response);
                    return true;
                }),
                catchError((response: HttpErrorResponse) => {
                    if (
                        response.status == 500 ||
                        response.error.message == null
                    ) {
                        this.toastService.showDanger(
                            'two-factor-auth.response.message.authentication_exception'
                        );
                    } else {
                        this.toastService.showDanger(
                            'two-factor-auth.' + response.error.message
                        );
                    }
                    return of(false);
                })
            );
    }

    loginSuccessfulHandler(
        userData: LoginResponse,
        group: AccessType,
        redirectToDashboard: boolean
    ) {
        this.saveUserData(userData);
        this.setAuthenticated(true);
        this.setCurrentGroup(group);
        this.scheduleRefreshSessionPopUp();
        this.translate.use(userData.language.toLowerCase());
        localStorage.setItem('language', userData.language.toLowerCase());

        localStorage.setItem(
            'light_theme',
            JSON.stringify(userData.lightThemePreferred)
        );

        if (redirectToDashboard) {
            this.router.navigate(['/dashboard']).then(() => {
                this.toastService.clearAll();
            });
        }
    }

    refreshToken() {
        this.http
            .post<LoginResponse>(`${this.appConfig.apiUrl}/refresh`, {
                login: this.getLogin(),
                refreshToken: this.getRefreshToken()
            })
            .pipe(
                tap((response) => {
                    this.loginSuccessfulHandler(
                        response,
                        this.getCurrentGroup(),
                        false
                    );
                }),
                catchError(() => {
                    this.logout();
                    this.toastService.showDanger('toast.auth.expired-session');
                    return EMPTY;
                })
            )
            .subscribe();
    }

    scheduleRefreshSessionPopUp() {
        const decodedJwtToken = this.getDecodedJwtToken(this.getJwt());
        const millisBeforeJwtExpires = decodedJwtToken.exp * 1000 - Date.now();
        this.scheduledRefresh = setTimeout(() => {
            this.modalService
                .open(RefreshSessionComponent)
                .result.then((refresh: boolean) => {
                    if (refresh) {
                        this.refreshToken();
                    } else {
                        if (!this.isJwtValid(this.getJwt())) {
                            this.logout();
                            this.toastService.showDanger(
                                'toast.auth.expired-session'
                            );
                        }
                    }
                })
                .catch(() => {
                    if (!this.isJwtValid(this.getJwt())) {
                        this.logout();
                        this.toastService.showDanger(
                            'toast.auth.expired-session'
                        );
                    }
                });
        }, millisBeforeJwtExpires * 0.9);
    }

    saveUserData(userData: LoginResponse) {
        const tokenInfo = this.getDecodedJwtToken(userData.jwt);
        this.setLogin(tokenInfo.sub);
        this.setJwt(userData.jwt);
        this.setRefreshToken(userData.refreshToken);
    }

    getGroups(): AccessType[] {
        const token = localStorage.getItem('jwt');
        if (token === null) {
            return [];
        }
        const tokenInfo = this.getDecodedJwtToken(token);
        return tokenInfo.groups;
    }

    getGroupsFromJwt(jwt: string | undefined): AccessType[] {
        if (jwt) {
            const tokenInfo = this.getDecodedJwtToken(jwt);
            return tokenInfo.groups;
        }
        return [];
    }

    getLogin(): string {
        return localStorage.getItem('login') ?? '';
    }

    getJwt(): string {
        return localStorage.getItem('jwt') ?? '';
    }

    getRefreshToken(): string {
        return localStorage.getItem('refreshToken') ?? '';
    }

    setLogin(login: string) {
        localStorage.setItem('login', login);
    }

    setJwt(jwt: string) {
        localStorage.setItem('jwt', jwt);
    }

    setRefreshToken(refreshToken: string) {
        localStorage.setItem('refreshToken', refreshToken);
    }

    getDecodedJwtToken(token: string): any {
        try {
            return jwtDecode(token);
        } catch (Error) {
            return null;
        }
    }

    isAuthenticated(): boolean {
        return this.authenticated.value;
    }

    setAuthenticated(value: boolean) {
        this.authenticated.next(value);
    }

    getCurrentGroup(): AccessType {
        return this.currentGroup.value;
    }

    setCurrentGroup(group: AccessType) {
        this.currentGroup.next(group);
        localStorage.setItem('currentGroup', group);
    }

    clearUserData() {
        localStorage.removeItem('login');
        localStorage.removeItem('jwt');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('currentGroup');
        localStorage.removeItem('language');
    }

    logout() {
        this.http
            .delete(
                `${
                    this.appConfig.apiUrl
                }/logout?token=${this.getRefreshToken()}`
            )
            .pipe(
                tap(() => {
                    this.handleLogout();
                }),
                catchError(() => {
                    this.handleLogout();
                    return EMPTY;
                })
            )
            .subscribe();
    }

    private handleLogout() {
        this.clearUserData();
        this.setAuthenticated(false);
        clearTimeout(this.scheduledRefresh);
        this.router.navigate(['/login']).then(() => {
            const browser_lang = this.translate.getBrowserLang();
            if (browser_lang != null) {
                this.translate.use(browser_lang);
            }
        });
    }

    isJwtValid(jwt: string): boolean {
        const decodedJwtToken = this.getDecodedJwtToken(jwt);
        if (decodedJwtToken) {
            return decodedJwtToken.exp * 1000 > Date.now();
        }
        return false;
    }

    isOwner() {
        return this.getCurrentGroup() === AccessLevels.OWNER;
    }

    isManager() {
        return this.getCurrentGroup() === AccessLevels.MANAGER;
    }

    isAdmin() {
        return this.getCurrentGroup() === AccessLevels.ADMIN;
    }

    getCurrentGroupObservable() {
        return this.currentGroup.asObservable();
    }
}
