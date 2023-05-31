import { Component, Input } from '@angular/core';
import { PlaceService } from '../../services/place.service';

@Component({
    selector: 'app-meters',
    templateUrl: './meters.component.html',
    styleUrls: ['./meters.component.css']
})
export class MetersComponent {
    @Input() placeId: number | undefined;

    constructor(private placeService: PlaceService) {}
}
