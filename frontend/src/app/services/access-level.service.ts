import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class AccessLevelService {
    private readonly BASE_URL = `${environment.apiUrl}/accounts`;

    constructor(private http: HttpClient) {}

    grantAccessLevel() {
        console.log('grant');
    }
}
