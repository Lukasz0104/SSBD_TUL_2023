import { Component } from '@angular/core';
import { PlaceService } from '../../services/place.service';
import { Observable } from 'rxjs';
import { OwnPlace } from '../../model/own-place';

@Component({
    selector: 'app-own-places',
    templateUrl: './own-places.component.html'
})
export class OwnPlacesComponent {
    ownPlaces$: Observable<OwnPlace[]>;

    toggled = false;
    chosenId: number | undefined;

    constructor(private placeService: PlaceService) {
        this.ownPlaces$ = this.placeService.getOwnPlaces();
    }

    showPlaceDetails(id: number) {
        this.chosenId = id;
        this.toggled = true;
    }

    hidePlaceDetails() {
        this.chosenId = undefined;
        this.toggled = false;
    }
}
