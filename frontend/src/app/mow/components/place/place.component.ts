import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Place } from '../../model/place';
import { AuthService } from '../../../shared/services/auth.service';

@Component({
    selector: 'app-place',
    templateUrl: './place.component.html'
})
export class PlaceComponent {
    @Input() places: Place[] | undefined;
    @Output() placeEdited = new EventEmitter();
    chosenId: number | undefined;
    toggled = false;
    tab = 1;

    constructor(protected authService: AuthService) {}

    showPlaceDetails(id: number) {
        this.tab = 1;
        this.chosenId = id;
        this.toggled = true;
    }

    hidePlaceDetails() {
        this.chosenId = undefined;
        this.toggled = false;
    }

    emitPlaceEdited() {
        this.placeEdited.emit();
    }
}
