import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { OwnPlace } from '../model/own-place';

@Injectable({
    providedIn: 'root'
})
export class PlaceService {
    private readonly BASE_URL = `${this.appConfig.apiUrl}/places`;

    constructor(
        private http: HttpClient,
        private appConfig: AppConfigService
    ) {}

    getOwnPlaces() {
        return this.http.get<OwnPlace[]>(`${this.BASE_URL}/me`);
    }
}
