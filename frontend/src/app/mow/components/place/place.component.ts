import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Place } from '../../model/place';
import { PlaceService } from '../../services/place.service';
import { ToastService } from '../../../shared/services/toast.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { PlaceCategoriesComponent } from '../place-categories/place-categories.component';
import { AuthService } from '../../../shared/services/auth.service';

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
        private toastService: ToastService,
        private modalService: NgbModal,
        protected authService: AuthService
    ) {}

    ngOnInit(): void {
        if (this.id === undefined) {
            this.loading = true;
            this.toastService.showDanger('toast.place.not-found');
        } else {
            if (this.authService.isOwner()) {
                this.place$ = this.placeService.getAsOwner(this.id);
            } else if (this.authService.isManager()) {
                this.place$ = this.placeService.getAsManager(this.id);
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
