import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { catchError, EMPTY, map, Observable, of } from 'rxjs';
import { Place } from '../model/place';
import { OwnPlaceCategory, PlaceCategory } from '../model/place-category';
import { Meter } from '../model/meter';
import { ToastService } from '../../shared/services/toast.service';

@Injectable({
    providedIn: 'root'
})
export class PlaceService {
    ifMatch = '';
    private readonly BASE_URL = `${this.config.apiUrl}/places`;

    constructor(
        private http: HttpClient,
        private config: AppConfigService,
        private toastService: ToastService
    ) {}

    getAsOwner(id: number): Observable<Place | null> {
        return this.http
            .get<Place>(`${this.BASE_URL}/me/${id}`, {
                observe: 'response'
            })
            .pipe(
                map((response) => {
                    this.ifMatch = response.headers.get('ETag') ?? '';
                    return response.body;
                })
            );
    }

    getAsManager(id: number): Observable<Place | null> {
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
        ['categories.elevator', 'bi-chevron-bar-expand'],
        ['categories.satellite_tv', 'bi-tv-fill'],
        ['categories.garbage', 'bi-trash3-fill text-secondary'],
        ['categories.maintenance', 'bi-cash-stack text-success'],
        ['categories.heating', 'bi-thermometer-sun text-danger'],
        ['categories.repair', 'bi-tools text-secondary'],
        ['categories.hot_water', 'bi-droplet text-danger'],
        ['categories.cold_water', 'bi-droplet text-primary'],
        ['categories.parking', 'bi-car-front-fill text-warning'],
        ['categories.intercom', 'bi-telephone']
    ]);

    getPlaceCategories(id: number) {
        return this.http.get<PlaceCategory[]>(
            `${this.BASE_URL}/${id}/categories`
        );
    }

    getPlaceMissingCategories(id: number) {
        return this.http
            .get<PlaceCategory[]>(`${this.BASE_URL}/${id}/categories/missing`)
            .pipe(
                catchError(() => {
                    this.toastService.showDanger(
                        'toast.place.get-missing-categories-fail'
                    );
                    return EMPTY;
                })
            );
    }

    checkIfReadingRequired(placeId: number, categoryId: number) {
        return this.http
            .get<boolean>(
                `${this.BASE_URL}/${placeId}/category/required_reading?categoryId=${categoryId}`
            )
            .pipe(
                catchError(() => {
                    this.toastService.showDanger(
                        'toast.place.check-if-reading-needed-fail'
                    );
                    return EMPTY;
                })
            );
    }

    addCategory(addCategoryDto: object) {
        return this.http
            .post(`${this.BASE_URL}/add/category`, addCategoryDto)
            .pipe(
                map(() => {
                    this.toastService.showSuccess(
                        'toast.place.add-category-success'
                    );
                    return of(true);
                }),
                catchError((err: HttpErrorResponse) => {
                    this.toastService.handleError(
                        'toast.place.add-category-fail',
                        'add-category',
                        err
                    );
                    return of(false);
                })
            );
    }

    getOwnPlaceCategories(id: number) {
        return this.http.get<OwnPlaceCategory[]>(
            `${this.BASE_URL}/me/${id}/categories`
        );
    }

    removeCategory(id: number | undefined, categoryId: number) {
        return this.http
            .delete(
                `${this.BASE_URL}/${id}/remove/category?categoryId=${categoryId}`
            )
            .pipe(
                map(() => {
                    this.toastService.showSuccess(
                        'toast.place.remove-category-success'
                    );
                    return of(true);
                }),
                catchError((err: HttpErrorResponse) => {
                    this.toastService.handleError(
                        'toast.place.remove-category-fail',
                        'remove-category',
                        err
                    );
                    return of(false);
                })
            );
    }

    getPlaceMetersAsOwner(id: number) {
        return this.http.get<Meter[]>(`${this.BASE_URL}/me/${id}/meters`);
    }

    getPlaceMetersAsManager(id: number) {
        return this.http.get<Meter[]>(`${this.BASE_URL}/${id}/meters`);
    }
}
