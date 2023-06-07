import { Component, Input, OnInit } from '@angular/core';
import { BehaviorSubject, catchError, EMPTY, map } from 'rxjs';
import { Place } from '../../model/place';
import { PlaceService } from '../../services/place.service';
import { ToastService } from '../../../shared/services/toast.service';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { PlaceCategoriesComponent } from '../place-categories/place-categories.component';
import { AuthService } from '../../../shared/services/auth.service';
import { AccessLevels } from '../../../shared/model/access-type';
import { PlaceEditComponent } from '../place-edit/place-edit.component';

@Component({
    selector: 'app-place',
    templateUrl: './place.component.html'
})
export class PlaceComponent implements OnInit {
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
            this.getPlace(this.id);
            this.loading = false;
        }
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

    editPlace() {
        this.place$
            .pipe(
                map((place: Place | null): void => {
                    if (place) {
                        const modalRef: NgbModalRef = this.modalService.open(
                            PlaceEditComponent,
                            {
                                centered: true,
                                size: 'xl',
                                scrollable: true
                            }
                        );
                        modalRef.componentInstance.setPlace(place);
                        modalRef.result.then((): void => {
                            this.getPlace(place.id);
                        });
                    }
                }),
                catchError(() => {
                    this.modalService.dismissAll();
                    return EMPTY;
                })
            )
            .subscribe();
    }

    protected readonly AccessLevels = AccessLevels;
}
