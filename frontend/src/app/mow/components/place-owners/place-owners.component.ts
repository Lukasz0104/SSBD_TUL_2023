import { Component, Input, OnInit } from '@angular/core';
import { PlaceService } from '../../services/place.service';
import { EMPTY, Observable } from 'rxjs';
import { PlaceOwner } from '../../model/place';

@Component({
    selector: 'app-place-owners',
    templateUrl: './place-owners.component.html'
})
export class PlaceOwnersComponent implements OnInit {
    protected owners$: Observable<PlaceOwner[]> = EMPTY;

    @Input()
    placeId!: number;

    constructor(private placeService: PlaceService) {}

    ngOnInit(): void {
        this.getOwners();
    }

    getOwners() {
        this.owners$ = this.placeService.getPlaceOwners(this.placeId);
    }

    removeOwner(ownerDataId: number) {
        this.placeService
            .removeOwner(ownerDataId, this.placeId)
            .subscribe(() => this.getOwners());
    }
}
