import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { BehaviorSubject } from 'rxjs';
import { LoginResponse } from '../model/login-response';
import jwtDecode from 'jwt-decode';
import { AccessLevel } from '../model/access-level';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { RefreshSessionComponent } from '../components/modals/refresh-session/refresh-session.component';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private authenticated = new BehaviorSubject<boolean>(false);
    private currentGroup = new BehaviorSubject<AccessLevel>(AccessLevel.NONE);

    constructor(private http: HttpClient, private modalService: NgbModal) {
        this.handleLocalStorageContent();
        this.addLocalStorageListener();
    }

    handleLocalStorageContent() {
        const jwt = localStorage.getItem('jwt');
        if (jwt) {
            const decodedJwt = this.getDecodedJwtToken(jwt);

            if (this.getDecodedJwtToken(jwt)) {
                const expiresAtMs = decodedJwt.exp * 1000;
                if (expiresAtMs < Date.now()) {
                    window.location.href = '/login';
                    this.clearUserData();
                }
                const group = localStorage.getItem('currentGroup');
                if (
                    group == AccessLevel.ADMIN ||
                    group == AccessLevel.MANAGER ||
                    group == AccessLevel.OWNER
                ) {
                    this.authenticated.next(true);
                    this.currentGroup.next(group as AccessLevel);
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
        return this.http.post<LoginResponse>(
            `${environment.apiUrl}/login`,
            { login, password },
            { observe: 'response' }
        );
    }

    refreshToken() {
        return this.http.post<LoginResponse>(
            `${environment.apiUrl}/refresh`,
            { login: this.getLogin(), refreshToken: this.getRefreshToken() },
            { observe: 'response' }
        );
    }

    scheduleRefreshSessionPopUp() {
        const decodedJwtToken = this.getDecodedJwtToken(this.getJwt());
        const millisBeforeJwtExpires = decodedJwtToken.exp * 1000 - Date.now();
        setTimeout(() => {
            this.modalService
                .open(RefreshSessionComponent)
                .result.then((refresh: boolean) => {
                    if (refresh) {
                        this.refreshToken().subscribe(
                            (result) => {
                                this.saveUserData(result);
                                this.scheduleRefreshSessionPopUp();
                            },
                            () => {
                                this.setAuthenticated(false);
                                this.clearUserData();
                            }
                        );
                    }
                })
                .catch(() => {
                    return false;
                });
        }, millisBeforeJwtExpires * 0.9);
    }

    saveUserData(result: HttpResponse<LoginResponse>) {
        if (result.body) {
            const tokenInfo = this.getDecodedJwtToken(result.body.jwt);
            this.setLogin(tokenInfo.sub);
            this.setJwt(result.body.jwt);
            this.setRefreshToken(result.body.refreshToken);
        }
    }

    getGroups(): AccessLevel[] {
        const token = localStorage.getItem('jwt');
        if (token === null) {
            return [];
        }
        const tokenInfo = this.getDecodedJwtToken(token);
        return tokenInfo.groups;
    }

    getGroupsFromJwt(jwt: string | undefined): AccessLevel[] {
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

    getCurrentGroup(): AccessLevel {
        return this.currentGroup.value;
    }

    setCurrentGroup(group: AccessLevel) {
        this.currentGroup.next(group);
        localStorage.setItem('currentGroup', group);
    }

    clearUserData() {
        localStorage.clear();
    }
}
