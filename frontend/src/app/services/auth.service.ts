import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { BehaviorSubject } from 'rxjs';
import { LoginResponse } from '../model/login-response';
import jwtDecode from 'jwt-decode';
import { AccessLevel } from '../model/access-level';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private authenticated = new BehaviorSubject<boolean>(false);
    private currentGroup = new BehaviorSubject<AccessLevel>(AccessLevel.NONE);

    constructor(private http: HttpClient) {
        const jwt = localStorage.getItem('jwt');
        if (jwt != null) {
            const expires = this.getDecodedJwtToken(jwt).exp;
            if (expires > Date.now()) {
                window.location.href = '/login';
                this.clearUserData();
            }
            if (this.getDecodedJwtToken(jwt)) {
                this.authenticated.next(true);
                this.currentGroup.next(
                    (<any>AccessLevel)[
                        localStorage.getItem('currentGroup') ?? ''
                    ]
                );
            }
        }
        window.addEventListener(
            'storage',
            (event) => {
                if (event.storageArea == localStorage) {
                    const token = localStorage.getItem('jwt');
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

    saveUserData(result: any) {
        const tokenInfo = this.getDecodedJwtToken(result.body.jwt);
        localStorage.setItem('login', tokenInfo.sub);
        localStorage.setItem('jwt', result.body.jwt);
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

    getDecodedJwtToken(token: string): any {
        try {
            return jwtDecode(token);
        } catch (Error) {
            return null;
        }
    }

    isAuthenticated() {
        return this.authenticated.value;
    }

    setAuthenticated(value: boolean) {
        this.authenticated.next(value);
    }

    getCurrentGroup() {
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
