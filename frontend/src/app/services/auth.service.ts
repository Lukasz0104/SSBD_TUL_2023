import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { BehaviorSubject, catchError, EMPTY, map, of, tap } from 'rxjs';
import { LoginResponse } from '../model/login-response';
import jwtDecode from 'jwt-decode';
import { AccessType } from '../model/access-type';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { RefreshSessionComponent } from '../components/modals/refresh-session/refresh-session.component';
import { ChooseAccessLevelComponent } from '../components/modals/choose-access-level/choose-access-level.component';
import { Router } from '@angular/router';
import { ToastService } from './toast.service';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private authenticated = new BehaviorSubject<boolean>(false);
    private currentGroup = new BehaviorSubject<AccessType>(AccessType.NONE);
    private scheduledRefresh: any;

    constructor(
        private http: HttpClient,
        private modalService: NgbModal,
        private router: Router,
        private toastService: ToastService,
        private translate: TranslateService
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
                    group == AccessType.ADMIN ||
                    group == AccessType.MANAGER ||
                    group == AccessType.OWNER
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
            .post<LoginResponse>(`${environment.apiUrl}/login`, {
                login,
                password
            })
            .pipe(
                map((response) => {
                    this.handleMultipleAccessLevels(response);
                    return true;
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

        if (redirectToDashboard) {
            this.router.navigate(['/dashboard']).then(() => {
                this.toastService.clearAll();
            });
        }
    }

    refreshToken() {
        this.http
            .post<LoginResponse>(`${environment.apiUrl}/refresh`, {
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
        localStorage.clear();
    }

    logout() {
        this.http
            .delete(
                `${environment.apiUrl}/logout?token=${this.getRefreshToken()}`
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
        return this.getCurrentGroup() === AccessType.OWNER;
    }

    isManager() {
        return this.getCurrentGroup() === AccessType.MANAGER;
    }

    isAdmin() {
        return this.getCurrentGroup() === AccessType.ADMIN;
    }
}
