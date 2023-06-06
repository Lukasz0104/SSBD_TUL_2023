import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EMPTY, Observable, map, mergeMap } from 'rxjs';
import { Place } from '../../model/place';
import { BuildingService } from '../../services/building.service';

@Component({
    selector: 'app-places',
    templateUrl: './places.component.html'
})
export class PlacesComponent implements OnInit {
    protected places$: Observable<Place[]> = EMPTY;

    constructor(
        private route: ActivatedRoute,
        private buildingService: BuildingService
    ) {}

    ngOnInit(): void {
        this.places$ = this.route.params.pipe(
            map((params) => params['id']),
            mergeMap((id) => {
                if (!id) return EMPTY;

                return this.buildingService.getPlacesInBuilding(+id);
            })
        );
    }
}
