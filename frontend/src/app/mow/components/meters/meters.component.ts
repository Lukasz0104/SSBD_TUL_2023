import { Component, Input, OnInit } from '@angular/core';
import { PlaceService } from '../../services/place.service';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Meter } from '../../model/meter';
import { AuthService } from '../../../shared/services/auth.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { MeterComponent } from '../meter/meter.component';

@Component({
    selector: 'app-meters',
    templateUrl: './meters.component.html'
})
export class MetersComponent implements OnInit {
    @Input() placeId: number | undefined;

    meters$: Observable<Meter[]> | undefined;

    openedMeter: BehaviorSubject<Meter> | undefined;

    constructor(
        private placeService: PlaceService,
        private authService: AuthService,
        private modalService: NgbModal
    ) {}

    ngOnInit() {
        this.getMeters();
    }

    public getIcon(category: string): string {
        return this.placeService.pictureMap.get(category) ?? 'bi-coin';
    }

    showMeterReadings(meter: Meter) {
        this.openedMeter = new BehaviorSubject<Meter>(meter);
        const ref = this.modalService.open(MeterComponent, {
            centered: true,
            size: 'xl',
            scrollable: true
        });
        ref.componentInstance.meter = this.openedMeter;
        ref.componentInstance.readingAdded.subscribe(() => {
            this.getMeters();
        });
    }

    getMeters() {
        if (this.placeId) {
            if (this.authService.isOwner()) {
                this.meters$ = this.placeService
                    .getPlaceMetersAsOwner(this.placeId)
                    .pipe(
                        tap((meters) => {
                            meters.forEach((m) => {
                                if (
                                    this.openedMeter &&
                                    m.id === this.openedMeter.getValue().id
                                ) {
                                    this.openedMeter.next(m);
                                }
                            });
                        })
                    );
            } else {
                this.meters$ = this.placeService
                    .getPlaceMetersAsManager(this.placeId)
                    .pipe(
                        tap((meters) => {
                            meters.forEach((m) => {
                                if (
                                    this.openedMeter &&
                                    m.id === this.openedMeter.getValue().id
                                ) {
                                    this.openedMeter.next(m);
                                }
                            });
                        })
                    );
            }
        }
    }
}
