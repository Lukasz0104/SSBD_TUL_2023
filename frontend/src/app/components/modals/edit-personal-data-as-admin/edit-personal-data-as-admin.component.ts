import { Component, Input } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from '../../../services/account.service';
import { AccessType } from '../../../model/access-type';
import { Account } from '../../../model/account';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Component({
    selector: 'app-edit-personal-data-as-admin',
    templateUrl: './edit-personal-data-as-admin.component.html'
})
export class EditPersonalDataAsAdminComponent {
    @Input() public account$: Observable<Account | null> | undefined;
    public newAccount: Account | null | undefined;

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
        ]),
        email: new FormControl('', [Validators.required, Validators.email]),
        language: new FormControl('', [
            Validators.required,
            Validators.pattern('^PL|EN$')
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
    ) {}

    setAccount(acc: Account) {
        this.account$ = this.accountService.getProfile(acc.id);
        this.account$.pipe(map((val) => (this.newAccount = val)));
        this.account$.subscribe({
            next: (val) => {
                this.newAccount = val;
                this.firstName = val?.firstName ?? '';
                this.lastName = val?.lastName ?? '';
                this.email = val?.email ?? '';
                this.language = val?.language ?? '';

                if (val?.accessLevels !== undefined) {
                    for (const level of val.accessLevels) {
                        switch (level.level) {
                            case AccessType.OWNER:
                                this.ownerStreet = level.address?.street ?? '';
                                this.ownerBuildingNumber =
                                    level.address?.buildingNumber ?? -1;
                                this.ownerCity = level.address?.city ?? '';
                                this.ownerPostalCode =
                                    level.address?.postalCode ?? '';
                                break;
                            case AccessType.MANAGER:
                                this.managerStreet =
                                    level.address?.street ?? '';
                                this.managerBuildingNumber =
                                    level.address?.buildingNumber ?? -1;
                                this.managerCity = level.address?.city ?? '';
                                this.managerPostalCode =
                                    level.address?.postalCode ?? '';
                                this.managerLicenseNumber =
                                    level.licenseNumber ?? '';
                                break;
                        }
                    }
                }
            }
        });
    }

    onSubmit() {
        if (this.newAccount != null) {
            this.newAccount.firstName = this.firstName;
            this.newAccount.lastName = this.lastName;
            this.newAccount.email = this.email;
            this.newAccount.language = this.language;
            for (const level of this.newAccount.accessLevels) {
                switch (level.level) {
                    case AccessType.OWNER:
                        if (level.address != null) {
                            level.address.street = this.ownerStreet;
                            level.address.buildingNumber =
                                this.ownerBuildingNumber;
                            level.address.city = this.ownerCity;
                            level.address.postalCode = this.ownerPostalCode;
                        }
                        break;
                    case AccessType.MANAGER:
                        if (level.address != null) {
                            level.address.street = this.managerStreet;
                            level.address.buildingNumber =
                                this.managerBuildingNumber;
                            level.address.city = this.managerCity;
                            level.address.postalCode = this.managerPostalCode;
                        }
                        level.licenseNumber = this.managerLicenseNumber;
                        break;
                }
            }

            this.accountService
                .editPersonalDataAsAdmin(this.newAccount)
                .subscribe((result) => {
                    if (result) {
                        this.activeModal.close();
                    }
                });
        }
    }

    areAllValid() {
        if (this.newAccount != null) {
            let valid = this.editPersonalDataForm.valid;
            for (const level of this.newAccount.accessLevels) {
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

    get email() {
        return this.editPersonalDataForm.get('email')?.getRawValue();
    }

    get emailControl() {
        return this.editPersonalDataForm.controls.email;
    }

    // eslint-disable-next-line @typescript-eslint/adjacent-overload-signatures
    set email(value: string) {
        this.editPersonalDataForm.controls.email.setValue(value ?? '');
    }

    get language() {
        return this.editPersonalDataForm.get('language')?.getRawValue();
    }

    get languageControl() {
        return this.editPersonalDataForm.controls.language;
    }

    // eslint-disable-next-line @typescript-eslint/adjacent-overload-signatures
    set language(value: string) {
        this.editPersonalDataForm.controls.language.setValue(value ?? '');
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

    get ownerBuildingNumberControl() {
        return this.editOwnerDataForm.controls.buildingNumber;
    }

    get ownerBuildingNumber() {
        return this.editOwnerDataForm.get('buildingNumber')?.getRawValue();
    }

    set ownerBuildingNumber(value: number) {
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

    get managerBuildingNumberControl() {
        return this.editManagerDataForm.controls.buildingNumber;
    }

    get managerBuildingNumber() {
        return this.editManagerDataForm.get('buildingNumber')?.getRawValue();
    }

    set managerBuildingNumber(value: number) {
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
    protected readonly environment = environment;
}
