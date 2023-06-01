import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppConfigService } from '../../shared/services/app-config.service';
import { Building } from '../../shared/model/building';

@Injectable({
    providedIn: 'root'
})
export class BuildingService {
    private readonly BASE_URL = `${this.config.apiUrl}/buildings`;

    constructor(private http: HttpClient, private config: AppConfigService) {}

    findAllBuildings(): Observable<Building[]> {
        return this.http.get<Building[]>(this.BASE_URL);
    }
}
