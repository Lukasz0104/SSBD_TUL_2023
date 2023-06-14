import { Component, Input, OnInit } from '@angular/core';
import { PlaceService } from '../../services/place.service';
import { EMPTY, Observable } from 'rxjs';
import { PlaceOwner } from '../../model/place';

@Component({
    selector: 'app-place-owners-add',
    templateUrl: './place-owners-add.component.html'
})
export class PlaceOwnersAddComponent implements OnInit {
    @Input()
    placeId!: number;
    protected notOwners$: Observable<PlaceOwner[]> = EMPTY;

    constructor(private placeService: PlaceService) {}

    ngOnInit(): void {
        this.getNotOwners();
    }

    getNotOwners() {
        this.notOwners$ = this.placeService.getPlaceOwners(this.placeId);
    }
}
