import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Account } from '../model/account';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    constructor(private http: HttpClient) {}

    getOwners() {
        return this.http.get<Account[]>(
            `${environment.apiUrl}/accounts/owners`
        );
    }

    getOwnersByActive(active: boolean) {
        return this.http.get<Account[]>(
            `${environment.apiUrl}/accounts/owners?active=${active}`
        );
    }
}
