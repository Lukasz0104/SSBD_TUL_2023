import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AccountService } from '../../services/account.service';
import { ToastService } from '../../services/toast.service';
import { strongPasswordValidator } from '../../validators/strong-password.validator';
import { repeatPasswordValidator } from '../../validators/repeat-password.validator';
import { diffPasswordValidator } from '../../validators/new-password.validator';
import { ConfirmActionComponent } from '../modals/confirm-action/confirm-action.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-change-password',
    templateUrl: './change-password.component.html'
})
export class ChangePasswordComponent {
    loading = false;
    showPassword = false;
    showRepeatPassword = false;
    showOldPassword = false;

    changePasswdForm = new FormGroup(
        {
            oldPassword: new FormControl('', [
                Validators.required,
                Validators.minLength(8)
            ]),
            password: new FormControl('', [
                Validators.required,
                strongPasswordValidator
            ]),
            repeatPassword: new FormControl('', [
                Validators.required,
                strongPasswordValidator
            ])
        },
        { validators: [repeatPasswordValidator, diffPasswordValidator] }
    );

    constructor(
        private accountService: AccountService,
        private toastService: ToastService,
        private modalService: NgbModal
    ) {}

    get password() {
        return this.changePasswdForm.get('password');
    }

    get repeatPassword() {
        return this.changePasswdForm.get('repeatPassword');
    }

    get oldPassword() {
        return this.changePasswdForm.get('oldPassword');
    }

    onSubmit() {
        this.loading = true;
        const password: string =
            this.changePasswdForm.getRawValue().password ?? '';
        const oldPassword: string =
            this.changePasswdForm.getRawValue().oldPassword ?? '';

        if (this.changePasswdForm.valid) {
            const dto: object = {
                newPassword: password,
                oldPassword: oldPassword
            };

            const modal = this.modalService.open(ConfirmActionComponent);
            const instance = modal.componentInstance as ConfirmActionComponent;

            instance.message = 'modal.confirm-action.change-password';
            instance.danger = ``;

            modal.closed.subscribe((res: boolean): void => {
                if (res) {
                    this.accountService.changePassword(dto).subscribe({
                        next: (res: boolean): void => {
                            if (res) {
                                this.changePasswdForm.reset();
                            }
                        },
                        error: (err) => this.toastService.showWarning(err)
                    });
                }
                this.loading = false;
            });
        }
    }

    get passwordControl() {
        return this.changePasswdForm.controls.password;
    }

    get passwordInvalid() {
        return this.passwordControl.invalid || this.samePassword;
    }

    get oldPasswordControl() {
        return this.changePasswdForm.controls.oldPassword;
    }

    get repeatPasswordControl() {
        return this.changePasswdForm.controls.repeatPassword;
    }

    protected get repeatPasswordInvalid() {
        return this.errorRepeatPassword() !== -1;
    }

    protected get passwordMismatch() {
        return this.changePasswdForm.errors?.['passwordMismatch'];
    }

    protected get samePassword() {
        return this.changePasswdForm.errors?.['samePassword'];
    }

    public errorRepeatPassword(): number {
        if (this.repeatPasswordControl.errors?.['required']) return 1;
        else if (this.samePassword) return 2;
        else if (this.passwordMismatch) return 3;
        return -1;
    }

    public errorPassword(): number {
        if (this.passwordControl.errors?.['required']) return 1;
        else if (this.samePassword) return 2;
        else if (this.passwordMismatch) return 3;
        else if (this.repeatPasswordControl.errors?.['weakPassword']) return 4;
        return -1;
    }
}
