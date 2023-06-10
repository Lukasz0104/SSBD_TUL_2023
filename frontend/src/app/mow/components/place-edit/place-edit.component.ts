import { Component, Input } from '@angular/core';
import { Place, PlaceEdit } from '../../model/place';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { PlaceService } from '../../services/place.service';
import { AuthService } from '../../../shared/services/auth.service';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
    selector: 'app-place-edit',
    templateUrl: './place-edit.component.html'
})
export class PlaceEditComponent {
    @Input() public place$: Observable<Place | null> | undefined;
    protected newPlace: Place | null | undefined;
    protected editPlaceFormGroup: FormGroup;

    constructor(
        public activeModal: NgbActiveModal,
        private modalService: NgbModal,
        private placeService: PlaceService,
        private authService: AuthService,
        private fb: FormBuilder
    ) {
        this.editPlaceFormGroup = this.fb.group({
            active: this.fb.control(true, [Validators.required]),
            squareFootage: this.fb.control(1, [
                Validators.required,
                Validators.min(0.001)
            ]),
            placeNumber: this.fb.control(1, [
                Validators.required,
                Validators.min(1)
            ]),
            residentsNumber: this.fb.control(1, [
                Validators.required,
                Validators.min(0),
                Validators.pattern('^[0-9]*$')
            ])
        });
    }

    setPlace(place: Place): void {
        this.getPlace(place.id);

        if (this.place$) {
            this.place$.subscribe({
                next: (val: Place | null): void => {
                    this.newPlace = val;
                    if (this.newPlace) {
                        this.active = this.newPlace.active ?? true;
                        this.squareFootage = this.newPlace.squareFootage ?? 1.0;
                        this.placeNumber = this.newPlace.placeNumber ?? 1;
                        this.residentsNumber =
                            this.newPlace.residentsNumber ?? 1;
                    }
                }
            });
        }
    }

    onSubmit(): void {
        const modalRef = this.modalService.open(ConfirmActionComponent);
        const instance = modalRef.componentInstance as ConfirmActionComponent;

        instance.message = 'modal.confirm-action.place-edit';
        instance.danger = '';
        modalRef.result.then((res: boolean): void => {
            if (res && this.newPlace) {
                const placeEdit: PlaceEdit = {
                    id: this.newPlace.id,
                    version: this.newPlace.version,
                    squareFootage: this.squareFootage,
                    placeNumber: this.placeNumber,
                    residentsNumber: this.residentsNumber,
                    active: this.active
                };

                this.placeService.editPlace(placeEdit).subscribe((result) => {
                    if (result) {
                        this.activeModal.close();
                    }
                });
            }
        });
    }

    getPlace(id: number) {
        if (this.authService.isOwner()) {
            this.place$ = this.placeService.getAsOwner(id);
        } else {
            this.place$ = this.placeService.getAsManager(id);
        }
    }

    // form controls
    get active() {
        return this.editPlaceFormGroup.get('active')?.getRawValue();
    }
    set active(value: boolean) {
        this.activeControl.setValue(value);
    }
    get activeControl() {
        return this.editPlaceFormGroup.controls['active'];
    }

    get squareFootage() {
        return this.editPlaceFormGroup.get('squareFootage')?.getRawValue();
    }
    set squareFootage(value: number) {
        this.squareFootageControl.setValue(value);
    }
    get squareFootageControl() {
        return this.editPlaceFormGroup.controls['squareFootage'];
    }

    get placeNumber() {
        return this.editPlaceFormGroup.get('placeNumber')?.getRawValue();
    }
    set placeNumber(value: number) {
        this.placeNumberControl.setValue(value);
    }
    get placeNumberControl() {
        return this.editPlaceFormGroup.controls['placeNumber'];
    }

    get residentsNumber() {
        return this.editPlaceFormGroup.get('residentsNumber')?.getRawValue();
    }
    set residentsNumber(value: number) {
        this.residentsNumberControl.setValue(value);
    }
    get residentsNumberControl() {
        return this.editPlaceFormGroup.controls['residentsNumber'];
    }

    areAllValid(): boolean {
        return this.editPlaceFormGroup.valid;
    }
}
