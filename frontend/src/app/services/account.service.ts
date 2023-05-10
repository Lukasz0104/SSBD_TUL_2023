import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { ToastService } from './toast.service';
import { Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    constructor(
        private http: HttpClient,
        private router: Router,
        private toastService: ToastService
    ) {}

    changePassword(dto: object) {
        return this.http.put(`${environment.apiUrl}/me/change-password`, {
            dto
        });
    }
}
