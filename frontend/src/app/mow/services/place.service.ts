import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { PlaceCategory } from '../model/place-category';

@Injectable({
    providedIn: 'root'
})
export class PlaceService {
    constructor(
        private http: HttpClient,
        private appConfig: AppConfigService
    ) {}

    private readonly placesUrl = `${this.appConfig.apiUrl}/places`;

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
            `${this.placesUrl}/${id}/categories`
        );
    }
}
