import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { OwnAccount } from '../model/account';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    constructor(private http: HttpClient) {}

    getOwnProfilelogin() {
        return this.http.get<OwnAccount>(`${environment.apiUrl}/accounts/me`);
    }
}
