import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { LoginResponse } from '../model/login-response';
import jwtDecode from 'jwt-decode';

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    authenticated = new BehaviorSubject(false);

    constructor(private http: HttpClient, private router: Router) {
        const jwt = localStorage.getItem('jwt');
        if (jwt != null) {
            const expires = this.getDecodedJwtToken(jwt).exp;
            if (expires > Date.now()) {
                window.location.href = '/login';
            }
            if (this.getDecodedJwtToken(jwt)) this.authenticated.next(true);
        }
        window.addEventListener(
            'storage',
            (event) => {
                if (event.storageArea == localStorage) {
                    const token = localStorage.getItem('jwt');
                    if (token == undefined) {
                        window.location.href = '/login';
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
        localStorage.setItem('groups', tokenInfo.groups);
    }

    getRole() {
        const token = localStorage.getItem('jwt');
        if (token === null) {
            return '';
        }
        const tokenInfo = this.getDecodedJwtToken(token);
        return tokenInfo.role;
    }

    getLogin() {
        return localStorage.getItem('login');
    }

    getDecodedJwtToken(token: string): any {
        try {
            return jwtDecode(token);
        } catch (Error) {
            return null;
        }
    }
}
