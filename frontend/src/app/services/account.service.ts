import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Account, OwnAccount } from '../model/account';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    constructor(private http: HttpClient) {}

    getOwnProfile() {
        return this.http.get<OwnAccount>(`${environment.apiUrl}/accounts/me`);
    }

    getProfile(id: number) {
        return this.http.get<Account>(`${environment.apiUrl}/accounts/${id}`);
    }
}
