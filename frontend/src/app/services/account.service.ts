import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Account } from '../model/account';
import { AccessType } from '../model/access-type';
import { of } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    constructor(private http: HttpClient) {}

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
}
