import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { AppConfigService } from '../../shared/services/app-config.service';
import { Place } from '../../shared/model/place';

@Injectable({
    providedIn: 'root'
})
export class PlaceService {
    ifMatch = '';
    private readonly BASE_URL = `${this.config.apiUrl}/places`;

    constructor(private http: HttpClient, private config: AppConfigService) {}

    get(id: number): Observable<Place | null> {
        return this.http
            .get<Place>(`${this.BASE_URL}/${id}`, {
                observe: 'response'
            })
            .pipe(
                map((response) => {
                    this.ifMatch = response.headers.get('ETag') ?? '';
                    return response.body;
                })
            );
    }
}
