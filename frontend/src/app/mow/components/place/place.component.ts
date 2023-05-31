import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Place } from '../../../shared/model/place';
import { PlaceService } from '../../services/place.service';
import { ActivatedRoute, Params } from '@angular/router';

@Component({
    selector: 'app-place',
    templateUrl: './place.component.html'
})
export class PlaceComponent {
    place$: Observable<Place | null> | undefined;
    id: number | undefined;
    loading = true;

    constructor(
        private placeService: PlaceService,
        private route: ActivatedRoute
    ) {
        this.route.queryParams.subscribe((params: Params): void => {
            this.id = params['id'];
            if (this.id !== undefined) {
                this.place$ = this.placeService.get(this.id);
            }
        });
    }
}
