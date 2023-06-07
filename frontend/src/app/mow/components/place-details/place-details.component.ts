import { Component, Input } from '@angular/core';
import { Place } from '../../model/place';

@Component({
    selector: 'app-place-details',
    templateUrl: './place-details.component.html'
})
export class PlaceDetailsComponent {
    @Input() place: Place | undefined;
    chosenId: number | undefined;
    toggled = false;
    tab = 1;

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
