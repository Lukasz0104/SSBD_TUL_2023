import { Component, Input } from '@angular/core';
import { PlaceService } from '../../services/place.service';
import { Observable } from 'rxjs';
import { Meter } from '../../model/meter';
import { AuthService } from '../../../shared/services/auth.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { MeterComponent } from '../meter/meter.component';

@Component({
    selector: 'app-meters',
    templateUrl: './meters.component.html'
})
export class MetersComponent {
    @Input() placeId: number | undefined;

    meters$: Observable<Meter[]> | undefined;

    constructor(
        private placeService: PlaceService,
        private authService: AuthService,
        private modalService: NgbModal
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

    showMeterReadings(meter: Meter) {
        const ref = this.modalService.open(MeterComponent, {
            centered: true,
            size: 'xl',
            scrollable: true
        });
        ref.componentInstance.meter = meter;
    }
    constructor(private placeService: PlaceService) {}
}
