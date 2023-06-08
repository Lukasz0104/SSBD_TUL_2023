import { Component, Input } from '@angular/core';
import { Place } from '../../model/place';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { PlaceService } from '../../services/place.service';
import { AuthService } from '../../../shared/services/auth.service';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
    selector: 'app-place-edit',
    templateUrl: './place-edit.component.html'
})
export class PlaceEditComponent {
    @Input() public place$: Observable<Place | null> | undefined;
    public newPlace: Place | null | undefined;

    constructor(
        public activeModal: NgbActiveModal,
        private modalService: NgbModal,
        private placeService: PlaceService,
        private authService: AuthService
    ) {}

    editPlaceFormGroup = new FormGroup({
        active: new FormControl(true, [Validators.required]),
        squareFootage: new FormControl(1.0, [
            Validators.required,
            Validators.min(1)
        ]),
        placeNumber: new FormControl(1, [
            Validators.required,
            Validators.pattern('[1-9][0-9]+')
        ]),
        residentsNumber: new FormControl(1, [
            Validators.required,
            Validators.min(1)
        ]),
        street: new FormControl('', [
            Validators.required,
            Validators.maxLength(85)
        ]),
        buildingNumber: new FormControl(1, [
            Validators.required,
            Validators.min(0)
        ]),
        postalCode: new FormControl('', [
            Validators.required,
            Validators.minLength(6),
            Validators.maxLength(6)
        ]),
        city: new FormControl('', [
            Validators.required,
            Validators.minLength(2),
            Validators.maxLength(85),
            Validators.pattern('[A-ZĄĆĘŁÓŚŹŻ]+.*')
        ])
    });

    setPlace(place: Place): void {
        if (this.authService.isOwner()) {
            this.place$ = this.placeService.getAsOwner(place.id);
        } else {
            this.place$ = this.placeService.getAsManager(place.id);
        }
        this.place$.subscribe({
            next: (val: Place | null): void => {
                this.newPlace = val;
                this.active = this.newPlace?.active ?? true;
                this.squareFootage = this.newPlace?.squareFootage ?? 1.0;
                this.placeNumber = this.newPlace?.placeNumber ?? 1;
                this.residentsNumber = this.newPlace?.residentsNumber ?? 1;
                this.buildingNumber =
                    this.newPlace?.building.address.buildingNumber ?? 1;
                this.street = this.newPlace?.building.address.street ?? '';
                this.postalCode =
                    this.newPlace?.building.address.postalCode ?? '';
                this.city = this.newPlace?.building.address.city ?? '';
            }
        });
    }

    onSubmit(): void {
        const modal = this.modalService.open(ConfirmActionComponent);
        const instance = modal.componentInstance as ConfirmActionComponent;

        instance.message = 'modal.confirm-action.place-edit';
        instance.danger = '';
        modal.closed.subscribe((res: boolean): void => {
            if (res && this.newPlace) {
                this.newPlace.active = this.active;
                this.newPlace.squareFootage = this.squareFootage;
                this.newPlace.placeNumber = this.placeNumber;
            }
        });
    }

    // form controls
    get active() {
        return this.editPlaceFormGroup.get('active')?.getRawValue();
    }
    set active(value: boolean) {
        this.activeControl.setValue(value);
    }
    get activeControl() {
        return this.editPlaceFormGroup.controls.active;
    }

    get squareFootage() {
        return this.editPlaceFormGroup.get('squareFootage')?.getRawValue();
    }
    set squareFootage(value: number) {
        this.squareFootageControl.setValue(value);
    }
    get squareFootageControl() {
        return this.editPlaceFormGroup.controls.squareFootage;
    }

    get placeNumber() {
        return this.editPlaceFormGroup.get('placeNumber')?.getRawValue();
    }
    set placeNumber(value: number) {
        this.placeNumberControl.setValue(value);
    }
    get placeNumberControl() {
        return this.editPlaceFormGroup.controls.placeNumber;
    }

    get residentsNumber() {
        return this.editPlaceFormGroup.get('residentsNumber')?.getRawValue();
    }
    set residentsNumber(value: number) {
        this.residentsNumberControl.setValue(value);
    }
    get residentsNumberControl() {
        return this.editPlaceFormGroup.controls.residentsNumber;
    }

    get street() {
        return this.editPlaceFormGroup.get('street')?.getRawValue();
    }
    set street(value: string) {
        this.streetControl.setValue(value);
    }
    get streetControl() {
        return this.editPlaceFormGroup.controls.street;
    }

    get buildingNumber() {
        return this.editPlaceFormGroup.get('buildingNumber')?.getRawValue();
    }
    set buildingNumber(value: number) {
        this.buildingNumberControl.setValue(value);
    }
    get buildingNumberControl() {
        return this.editPlaceFormGroup.controls.buildingNumber;
    }

    get postalCode() {
        return this.editPlaceFormGroup.get('postalCode')?.getRawValue();
    }
    set postalCode(value: string) {
        this.postalCodeControl.setValue(value);
    }
    get postalCodeControl() {
        return this.editPlaceFormGroup.controls.postalCode;
    }

    get city() {
        return this.editPlaceFormGroup.get('city')?.getRawValue();
    }
    set city(value: string) {
        this.cityControl.setValue(value);
    }
    get cityControl() {
        return this.editPlaceFormGroup.controls.city;
    }
}
