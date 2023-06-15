import { Injectable } from '@angular/core';
import {
    HttpClient,
    HttpErrorResponse,
    HttpHeaders
} from '@angular/common/http';
import { AppConfigService } from '../../shared/services/app-config.service';
import { catchError, EMPTY, map, Observable, of, tap } from 'rxjs';
import { CreatePlaceDto, Place, PlaceEdit, PlaceOwner } from '../model/place';
import { OwnPlaceCategory, PlaceCategory } from '../model/place-category';
import { Meter } from '../model/meter';
import { ToastService } from '../../shared/services/toast.service';

type MessageResponse = { message: string };

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

    private getPlace(url: string) {
        return this.http
            .get<Place>(url, {
                observe: 'response'
            })
            .pipe(
                map((response) => {
                    this.ifMatch = response.headers.get('ETag') ?? '';
                    return response.body;
                }),
                catchError((err: HttpErrorResponse) => {
                    this.handleError('toast.place.fail', 'toast.place', err);
                    return EMPTY;
                })
            );
    }

    getAsOwner(id: number): Observable<Place | null> {
        return this.getPlace(`${this.BASE_URL}/me/${id}`);
    }

    getAsManager(id: number): Observable<Place | null> {
        return this.getPlace(`${this.BASE_URL}/${id}`);
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

    addCategory(
        placeId: number,
        categoryId: number,
        newReading: number | null
    ) {
        return this.http
            .post(`${this.BASE_URL}/add/category`, {
                placeId,
                categoryId,
                newReading
            })
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
            .delete(`${this.BASE_URL}/${id}/categories/${categoryId}`)
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

    addPlace(dto: CreatePlaceDto): Observable<string | null> {
        return this.http.post<MessageResponse | null>(this.BASE_URL, dto).pipe(
            map((response) => response?.message),
            catchError((e: HttpErrorResponse) => of(e.error.message))
        );
    }

    handleError(
        genericMessageKey: string,
        method: string,
        response: HttpErrorResponse
    ): void {
        if (response.status == 500 || response.error.message == null) {
            this.toastService.showDanger(genericMessageKey);
        } else {
            this.toastService.showDanger(method + '.' + response.error.message);
        }
    }

    editPlace(newPlace: PlaceEdit) {
        return this.http
            .put<Place>(`${this.BASE_URL}/${newPlace.id}`, newPlace, {
                headers: new HttpHeaders({ 'If-Match': this.ifMatch }),
                observe: 'response'
            })
            .pipe(
                tap(() => {
                    this.toastService.showSuccess('toast.place-edit.success');
                }),
                map(() => true),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        'toast.place-edit.fail',
                        'toast.place-edit',
                        err
                    );
                    return of(true);
                })
            );
    }

    getPlaceOwners(placeId: number): Observable<PlaceOwner[]> {
        return this.http.get<PlaceOwner[]>(
            `${this.BASE_URL}/${placeId}/owners`
        );
    }

    getPlaceNotOwners(placeId: number): Observable<PlaceOwner[]> {
        return this.http.get<PlaceOwner[]>(
            `${this.BASE_URL}/${placeId}/not-owners`
        );
    }

    addOwner(ownerId: number, placeId: number) {
        return this.http
            .post(`${this.BASE_URL}/${placeId}/owners`, null, {
                params: { ownerId: ownerId }
            })
            .pipe(
                tap(() => {
                    this.toastService.showSuccess(
                        `toast.place-owners-add.success`
                    );
                }),
                map(() => true),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        `toast.place-owners-add.fail`,
                        `toast.place-owners-add`,
                        err
                    );
                    return of(true);
                })
            );
    }

    removeOwner(ownerId: number, placeId: number) {
        return this.http
            .delete(`${this.BASE_URL}/${placeId}/owners`, {
                params: { ownerId: ownerId }
            })
            .pipe(
                tap(() => {
                    this.toastService.showSuccess(
                        `toast.place-owners-remove.success`
                    );
                }),
                map(() => true),
                catchError((err: HttpErrorResponse) => {
                    this.handleError(
                        `toast.place-owners-remove.fail`,
                        `toast.place-owners-remove`,
                        err
                    );
                    return of(true);
                })
            );
    }
}
