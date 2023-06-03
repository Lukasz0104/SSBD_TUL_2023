import { Component, Input, OnInit } from '@angular/core';
import { PlaceService } from '../../services/place.service';
import { Observable } from 'rxjs';
import { Meter } from '../../model/meter';
import { AuthService } from '../../../shared/services/auth.service';

@Component({
    selector: 'app-meters',
    templateUrl: './meters.component.html'
})
export class MetersComponent implements OnInit {
    @Input() placeId: number | undefined;

    meters$: Observable<Meter[]> | undefined;

    constructor(
        private placeService: PlaceService,
        private authService: AuthService
    ) {}

    ngOnInit() {
        if (this.placeId) {
            if (this.authService.isOwner()) {
                this.meters$ = this.placeService.getPlaceMetersAsOwner(
                    this.placeId
                );
            } else {
                this.meters$ = this.placeService.getPlaceMetersAsManager(
                    this.placeId
                );
            }
        }
    }

    public getIcon(category: string): string {
        return this.placeService.pictureMap.get(category) ?? 'bi-coin';
    }
}
