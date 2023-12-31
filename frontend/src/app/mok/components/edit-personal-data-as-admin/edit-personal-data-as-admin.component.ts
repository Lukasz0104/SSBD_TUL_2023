import { Component, Input } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from '../../../shared/services/account.service';
import { AccessLevels } from '../../../shared/model/access-type';
import { Account } from '../../../shared/model/account';
import {
    debounceTime,
    distinctUntilChanged,
    map,
    Observable,
    of,
    OperatorFunction,
    switchMap
} from 'rxjs';
import { ConfirmActionComponent } from '../../../shared/components/confirm-action/confirm-action.component';
import { AppConfigService } from '../../../shared/services/app-config.service';
import { postalCodeValidator } from '../../../shared/validators/postal-code.validator';
import { validLicenseNumberValidator } from '../../../shared/validators/valid-license-number.validator';

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
            Validators.maxLength(6),
            postalCodeValidator
        ]),
        city: new FormControl('', [
            Validators.required,
            Validators.minLength(2),
            Validators.maxLength(85),
            Validators.pattern('[A-ZĄĆĘŁÓŚŹŻ]+.*')
        ]),
        licenseNumber: new FormControl('', [
            Validators.required,
            validLicenseNumberValidator
        ])
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
            Validators.maxLength(6),
            postalCodeValidator
        ]),
        city: new FormControl('', [
            Validators.required,
            Validators.minLength(2),
            Validators.maxLength(85),
            Validators.pattern('[A-ZĄĆĘŁÓŚŹŻ]+.*')
        ])
    });

    constructor(
        public activeModal: NgbActiveModal,
        private accountService: AccountService,
        private modalService: NgbModal,
        protected appConfig: AppConfigService
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
                            case AccessLevels.OWNER:
                                this.ownerStreet = level.address?.street ?? '';
                                this.ownerBuildingNumber =
                                    level.address?.buildingNumber ?? -1;
                                this.ownerCity = level.address?.city ?? '';
                                this.ownerPostalCode =
                                    level.address?.postalCode ?? '';
                                break;
                            case AccessLevels.MANAGER:
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

    onSubmit(): void {
        const modal = this.modalService.open(ConfirmActionComponent);
        const instance = modal.componentInstance as ConfirmActionComponent;

        instance.message = 'modal.confirm-action.edit-account';
        instance.danger = `${this.newAccount?.login}`;

        modal.closed.subscribe((res: boolean): void => {
            if (res) {
                if (this.newAccount != null) {
                    this.newAccount.firstName = this.firstName;
                    this.newAccount.lastName = this.lastName;
                    this.newAccount.email = this.email;
                    this.newAccount.language = this.language;
                    for (const level of this.newAccount.accessLevels) {
                        switch (level.level) {
                            case AccessLevels.OWNER:
                                if (level.address != null) {
                                    level.address.street = this.ownerStreet;
                                    level.address.buildingNumber =
                                        this.ownerBuildingNumber;
                                    level.address.city = this.ownerCity;
                                    level.address.postalCode =
                                        this.ownerPostalCode;
                                }
                                break;
                            case AccessLevels.MANAGER:
                                if (level.address != null) {
                                    level.address.street = this.managerStreet;
                                    level.address.buildingNumber =
                                        this.managerBuildingNumber;
                                    level.address.city = this.managerCity;
                                    level.address.postalCode =
                                        this.managerPostalCode;
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
        });
    }

    areAllValid() {
        if (this.newAccount != null) {
            let valid = this.editPersonalDataForm.valid;
            for (const level of this.newAccount.accessLevels) {
                switch (level.level) {
                    case AccessLevels.OWNER:
                        valid = valid && this.editOwnerDataForm.valid;
                        break;
                    case AccessLevels.MANAGER:
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

    searchOwner: OperatorFunction<string, readonly string[]> = (
        text$: Observable<string>
    ) =>
        text$.pipe(
            debounceTime(200),
            distinctUntilChanged(),
            switchMap((value: string) => {
                if (!this.ownerCityControl.valid) {
                    return of([]);
                }
                return this.accountService.getCitiesByPattern(value);
            })
        );

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

    searchManager: OperatorFunction<string, readonly string[]> = (
        text$: Observable<string>
    ) =>
        text$.pipe(
            debounceTime(200),
            distinctUntilChanged(),
            switchMap((value: string) => {
                if (!this.managerCityControl.valid) {
                    return of([]);
                }
                return this.accountService.getCitiesByPattern(value);
            })
        );

    //endregion

    protected readonly AccessType = AccessLevels;
    protected readonly AccessLevels = AccessLevels;
}
