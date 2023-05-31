import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Place } from '../../model/place';
import { PlaceService } from '../../services/place.service';
import { ToastService } from '../../../shared/services/toast.service';

@Component({
    selector: 'app-place',
    templateUrl: './place.component.html'
})
export class PlaceComponent implements OnInit {
    place$: Observable<Place | null> | undefined;
    @Input() id: number | undefined;
    loading = true;

    constructor(
        private placeService: PlaceService,
        private toastService: ToastService
    ) {}

    ngOnInit(): void {
        if (this.id === undefined) {
            this.loading = true;
            this.toastService.showDanger('toast.place.not-found');
        } else {
            this.place$ = this.placeService.get(this.id);
            this.loading = false;
        }
    }
}
