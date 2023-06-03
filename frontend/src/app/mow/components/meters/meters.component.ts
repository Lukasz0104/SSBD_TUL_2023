import { Component, Input } from '@angular/core';
import { PlaceService } from '../../services/place.service';

@Component({
    selector: 'app-meters',
    templateUrl: './meters.component.html'
})
export class MetersComponent {
    @Input() placeId: number | undefined;

    constructor(private placeService: PlaceService) {}
}
