import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { map, Observable } from 'rxjs';
import { Place } from '../model/place';
import { PlaceCategory } from '../model/place-category';

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

    getOwnPlaces() {
        return this.http.get<Place[]>(`${this.BASE_URL}/me`);
    }

    public pictureMap = new Map<string, string>([
        ['Winda', 'bi-chevron-bar-expand'],
        ['Telewizja satelitarna', 'bi-tv-fill'],
        ['Śmieci', 'bi-trash3-fill text-secondary'],
        ['Opłata eksploatacyjna', 'bi-cash-stack text-success'],
        ['Ogrzewanie', 'bi-thermometer-sun text-danger'],
        ['Opłata remontowa', 'bi-tools text-secondary'],
        ['Woda ciepła', 'bi-droplet text-danger'],
        ['Woda zimna', 'bi-droplet text-primary'],
        ['Parking', 'bi-car-front-fill text-warning'],
        ['Domofon', 'bi-telephone']
    ]);

    getPlaceCategories(id: number) {
        return this.http.get<PlaceCategory[]>(
            `${this.BASE_URL}/${id}/categories`
        );
    }
}
