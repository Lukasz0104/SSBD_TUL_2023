import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EMPTY, Observable, map, mergeMap, tap } from 'rxjs';
import { Place } from '../../model/place';
import { BuildingService } from '../../services/building.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AddPlaceComponent } from '../add-place/add-place.component';

@Component({
    selector: 'app-places',
    templateUrl: './places.component.html'
})
export class PlacesComponent implements OnInit {
    protected places$: Observable<Place[]> = EMPTY;
    protected buildingId: number | undefined;

    constructor(
        private route: ActivatedRoute,
        private buildingService: BuildingService,
        private modalService: NgbModal
    ) {}

    ngOnInit(): void {
        this.places$ = this.route.params.pipe(
            map((params) => params['id']),
            tap((id) => (this.buildingId = id)),
            mergeMap((id) => {
                if (!id) return EMPTY;

                return this.buildingService.getPlacesInBuilding(+id);
            })
        );
    }

    openModalToAddPlace() {
        if (this.buildingId) {
            const modal = this.modalService.open(AddPlaceComponent);
            const instance = modal.componentInstance as AddPlaceComponent;

            instance.buildingId = this.buildingId;

            modal.closed.subscribe(() => this.refresh());
        }
    }

    refresh() {
        if (this.buildingId) {
            this.places$ = this.buildingService.getPlacesInBuilding(
                this.buildingId
            );
        }
    }
}
