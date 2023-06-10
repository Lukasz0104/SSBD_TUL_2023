import { Component, Input, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Place } from '../../model/place';
import { PlaceService } from '../../services/place.service';
import { ToastService } from '../../../shared/services/toast.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { PlaceCategoriesComponent } from '../place-categories/place-categories.component';
import { AuthService } from '../../../shared/services/auth.service';

@Component({
    selector: 'app-place-details',
    templateUrl: './place-details.component.html'
})
export class PlaceDetailsComponent implements OnInit {
    place$ = new BehaviorSubject<Place | null>(null);

    @Input() id: number | undefined;

    @Input() place: Place | undefined;

    loading = true;

    constructor(
        private placeService: PlaceService,
        private toastService: ToastService,
        private modalService: NgbModal,
        protected authService: AuthService
    ) {}

    ngOnInit(): void {
        if (this.place) {
            this.place$.next(this.place);
            this.loading = false;
        } else if (this.id === undefined) {
            this.loading = true;
            this.toastService.showDanger('toast.place.not-found');
        } else {
            if (this.authService.isOwner()) {
                this.placeService
                    .getAsOwner(this.id)
                    .subscribe((place) => this.place$.next(place));
            } else if (this.authService.isManager()) {
                this.placeService
                    .getAsManager(this.id)
                    .subscribe((place) => this.place$.next(place));
            } else {
                this.toastService.showDanger('toast.guard.access-denied');
                return;
            }
            this.loading = false;
        }
    }

    placeCategories(id: number) {
        const modalRef: NgbModalRef = this.modalService.open(
            PlaceCategoriesComponent,
            {
                centered: true,
                size: 'xl'
            }
        );
        modalRef.componentInstance.id = id;
    }
}
