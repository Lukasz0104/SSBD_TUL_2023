import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { Observable } from 'rxjs';
import { Page } from '../../shared/model/page';
import { Reading } from '../model/reading';

@Injectable({
    providedIn: 'root'
})
export class MeterService {
    private readonly BASE_URL = `${this.config.apiUrl}/meters`;

    constructor(private http: HttpClient, private config: AppConfigService) {}

    getReadingsAsOwner(
        meterId: number,
        page: number,
        pageSize: number
    ): Observable<Page<Reading>> {
        return this.http.get<Page<Reading>>(
            `${this.BASE_URL}/me/${meterId}/readings?page=${page}&pageSize=${pageSize}`
        );
    }

    getReadingsAsManager(
        meterId: number,
        page: number,
        pageSize: number
    ): Observable<Page<Reading>> {
        return this.http.get<Page<Reading>>(
            `${this.BASE_URL}/${meterId}/readings?page=${page}&pageSize=${pageSize}`
        );
    }
}
