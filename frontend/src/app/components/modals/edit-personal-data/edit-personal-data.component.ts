import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EditPersonalData, OwnAccount } from '../../../model/account';
import { AccountService } from '../../../services/account.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Observable, tap } from 'rxjs';
import { AccessType } from '../../../model/access-type';

@Component({
    selector: 'app-edit-personal-data',
    templateUrl: './edit-personal-data.component.html'
})
export class EditPersonalDataComponent {
    ownAccount$: Observable<OwnAccount | null>;
    ownAccount: EditPersonalData | null | undefined;

    editPersonalDataForm = new FormGroup({
        firstName: new FormControl('', [
            Validators.required,
            Validators.minLength(1),
            Validators.maxLength(100)
        ]),
        lastName: new FormControl('', [
            Validators.required,
            Validators.minLength(1),
            Validators.maxLength(100)
        ])
    });

    editManagerDataForm = new FormGroup({
        street: new FormControl('', [
            Validators.required,
            Validators.maxLength(85)
        ]),
        buildingNumber: new FormControl('', [
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
            Validators.maxLength(85)
        ]),
        licenseNumber: new FormControl('', [Validators.required])
    });

    editOwnerDataForm = new FormGroup({
        street: new FormControl('', [
            Validators.required,
            Validators.maxLength(85)
        ]),
        buildingNumber: new FormControl('', [
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
            Validators.maxLength(85)
        ])
    });

    constructor(
        public activeModal: NgbActiveModal,
        private accountService: AccountService
    ) {
        this.ownAccount$ = accountService.getOwnProfile().pipe(
            tap((result) => {
                if (result) {
                    const { language, email, id, ...editData } = result;
                    this.ownAccount = editData;
                    this.firstName = result?.firstName ?? '';
                    this.lastName = result?.lastName ?? '';

                    if (result?.accessLevels !== undefined) {
                        for (const level of result.accessLevels) {
                            switch (level.level) {
                                case AccessType.OWNER:
                                    this.ownerStreet =
                                        level.address?.street ?? '';
                                    this.ownerBuldingNumber =
                                        level.address?.buildingNumber ?? -1;
                                    this.ownerCity = level.address?.city ?? '';
                                    this.ownerPostalCode =
                                        level.address?.postalCode ?? '';
                                    break;
                                case AccessType.MANAGER:
                                    this.managerStreet =
                                        level.address?.street ?? '';
                                    this.managerBuldingNumber =
                                        level.address?.buildingNumber ?? -1;
                                    this.managerCity =
                                        level.address?.city ?? '';
                                    this.managerPostalCode =
                                        level.address?.postalCode ?? '';
                                    this.managerLicenseNumber =
                                        level.licenseNumber ?? '';
                                    break;
                            }
                        }
                    }
                }
            })
        );
    }

    onSubmit() {
        if (this.ownAccount != null) {
            this.ownAccount.firstName = this.firstName;
            this.ownAccount.lastName = this.lastName;
            for (const level of this.ownAccount.accessLevels) {
                switch (level.level) {
                    case AccessType.OWNER:
                        if (level.address != null) {
                            level.address.street = this.ownerStreet;
                            level.address.buildingNumber =
                                this.ownerBuldingNumber;
                            level.address.city = this.ownerCity;
                            level.address.postalCode = this.ownerPostalCode;
                        }
                        break;
                    case AccessType.MANAGER:
                        if (level.address != null) {
                            level.address.street = this.managerStreet;
                            level.address.buildingNumber =
                                this.managerBuldingNumber;
                            level.address.city = this.managerCity;
                            level.address.postalCode = this.managerPostalCode;
                        }
                        level.licenseNumber = this.managerLicenseNumber;
                        break;
                }
            }

            this.accountService
                .editOwnProfile(this.ownAccount)
                .subscribe((result) => {
                    if (result) {
                        this.activeModal.close();
                    }
                });
        }
    }

    areAllValid() {
        if (this.ownAccount != null) {
            let valid = this.editPersonalDataForm.valid;
            for (const level of this.ownAccount.accessLevels) {
                switch (level.level) {
                    case AccessType.OWNER:
                        valid = valid && this.editOwnerDataForm.valid;
                        break;
                    case AccessType.MANAGER:
                        valid = valid && this.editManagerDataForm.valid;
                        break;
                }
            }
            return valid;
        } else {
            return false;
        }
    }

    get firstNameControl() {
        return this.editPersonalDataForm.controls.firstName;
    }

    get firstName() {
        return this.editPersonalDataForm.get('firstName')?.getRawValue();
    }

    set firstName(value: string) {
        this.editPersonalDataForm.controls.firstName.setValue(value ?? '');
    }

    get lastNameControl() {
        return this.editPersonalDataForm.controls.lastName;
    }

    get lastName() {
        return this.editPersonalDataForm.get('lastName')?.getRawValue();
    }

    set lastName(value: string) {
        this.editPersonalDataForm.controls.lastName.setValue(value ?? '');
    }

    //region owner

    get ownerStreetControl() {
        return this.editOwnerDataForm.controls.street;
    }

    get ownerStreet() {
        return this.editOwnerDataForm.get('street')?.getRawValue();
    }

    set ownerStreet(value: string) {
        this.editOwnerDataForm.controls.street.setValue(value ?? '');
    }

    get ownerBuldingNumberControl() {
        return this.editOwnerDataForm.controls.buildingNumber;
    }

    get ownerBuldingNumber() {
        return this.editOwnerDataForm.get('buildingNumber')?.getRawValue();
    }

    set ownerBuldingNumber(value: number) {
        this.editOwnerDataForm.controls.buildingNumber.setValue(
            String(value) ?? ''
        );
    }

    get ownerPostalCodeControl() {
        return this.editOwnerDataForm.controls.postalCode;
    }

    get ownerPostalCode() {
        return this.editOwnerDataForm.get('postalCode')?.getRawValue();
    }

    set ownerPostalCode(value: string) {
        this.editOwnerDataForm.controls.postalCode.setValue(value ?? '');
    }

    get ownerCityControl() {
        return this.editOwnerDataForm.controls.city;
    }

    get ownerCity() {
        return this.editOwnerDataForm.get('city')?.getRawValue();
    }

    set ownerCity(value: string) {
        this.editOwnerDataForm.controls.city.setValue(value ?? '');
    }

    //endregion

    //region manager

    get managerStreetControl() {
        return this.editManagerDataForm.controls.street;
    }

    get managerStreet() {
        return this.editManagerDataForm.get('street')?.getRawValue();
    }

    set managerStreet(value: string) {
        this.editManagerDataForm.controls.street.setValue(value ?? '');
    }

    get managerBuldingNumberControl() {
        return this.editManagerDataForm.controls.buildingNumber;
    }

    get managerBuldingNumber() {
        return this.editManagerDataForm.get('buildingNumber')?.getRawValue();
    }

    set managerBuldingNumber(value: number) {
        this.editManagerDataForm.controls.buildingNumber.setValue(
            String(value) ?? ''
        );
    }

    get managerPostalCodeControl() {
        return this.editManagerDataForm.controls.postalCode;
    }

    get managerPostalCode() {
        return this.editManagerDataForm.get('postalCode')?.getRawValue();
    }

    set managerPostalCode(value: string) {
        this.editManagerDataForm.controls.postalCode.setValue(value ?? '');
    }

    get managerCityControl() {
        return this.editManagerDataForm.controls.city;
    }

    get managerCity() {
        return this.editManagerDataForm.get('city')?.getRawValue();
    }

    set managerCity(value: string) {
        this.editManagerDataForm.controls.city.setValue(value ?? '');
    }

    get managerLicenseNumberControl() {
        return this.editManagerDataForm.controls.licenseNumber;
    }

    get managerLicenseNumber() {
        return this.editManagerDataForm.get('licenseNumber')?.getRawValue();
    }

    set managerLicenseNumber(value: string) {
        this.editManagerDataForm.controls.licenseNumber.setValue(value ?? '');
    }

    //endregion

    protected readonly AccessType = AccessType;
}
