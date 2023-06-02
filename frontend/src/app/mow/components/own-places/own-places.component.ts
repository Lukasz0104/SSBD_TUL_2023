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

    toggled = false;
    chosenId: number | undefined;

    tab = 1;

    constructor(private placeService: PlaceService) {
        this.ownPlaces$ = this.placeService.getOwnPlaces();
    }

    showPlaceDetails(id: number) {
        this.tab = 1;
        this.chosenId = id;
        this.toggled = true;
    }

    hidePlaceDetails() {
        this.chosenId = undefined;
        this.toggled = false;
    }
}
