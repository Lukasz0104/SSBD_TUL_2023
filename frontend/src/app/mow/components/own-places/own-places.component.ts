import { Component } from '@angular/core';
import { PlaceService } from '../../services/place.service';
import { Observable } from 'rxjs';
import { Place } from '../../model/place';

@Component({
    selector: 'app-own-places',
    templateUrl: './own-places.component.html'
})
export class OwnPlacesComponent {
    ownPlaces$: Observable<Place[]>;

    constructor(private placeService: PlaceService) {
        this.ownPlaces$ = this.placeService.getOwnPlaces();
    }
}
