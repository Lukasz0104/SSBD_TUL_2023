import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Place } from '../../model/place';
import { PlaceService } from '../../services/place.service';
import { ToastService } from '../../../shared/services/toast.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from '../../../shared/services/auth.service';
import { PlaceEditComponent } from '../place-edit/place-edit.component';

@Component({
    selector: 'app-place-details',
    templateUrl: './place-details.component.html'
})
export class PlaceDetailsComponent implements OnInit {
    place$ = new BehaviorSubject<Place | null>(null);
    @Input() id: number | undefined;
    @Input() place: Place | undefined;
    @Output() placeEdited = new EventEmitter();
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
            this.getPlace(this.id);
            this.loading = false;
        }
    }

    editPlace(id: number) {
        const modalRef: NgbModalRef = this.modalService.open(
            PlaceEditComponent,
            { centered: true }
        );
        modalRef.componentInstance.setPlace(id);
        modalRef.result
            .then((): void => {
                this.getPlace(id);
                this.placeEdited.emit();
            })
            .catch(() => this.placeEdited.emit());
    }

    getPlace(id: number) {
        if (this.authService.isOwner()) {
            this.placeService
                .getAsOwner(id)
                .subscribe((place: Place | null) => this.place$.next(place));
        } else if (this.authService.isManager()) {
            this.placeService
                .getAsManager(id)
                .subscribe((place: Place | null) => this.place$.next(place));
        } else {
            this.toastService.showDanger('toast.guard.access-denied');
            return;
        }
    }
}
