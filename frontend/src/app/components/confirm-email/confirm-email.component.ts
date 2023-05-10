import { Component, OnInit } from '@angular/core';
import { ChangeEmailService } from '../../services/change-email.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastService } from '../../services/toast.service';

@Component({
    selector: 'app-confirm-email',
    templateUrl: './confirm-email.component.html'
})
export class ConfirmEmailComponent implements OnInit {
    changeEmailForm = new FormGroup({
        email: new FormControl('', [Validators.required, Validators.email])
    });
    loading = false;

    constructor(
        private route: ActivatedRoute,
        private emailService: ChangeEmailService,
        private toastService: ToastService,
        private router: Router
    ) {}

    _tokenId: string | undefined;

    get tokenId(): string | undefined {
        return this._tokenId;
    }

    get email() {
        return this.changeEmailForm.get('email');
    }

    ngOnInit(): void {
        this.route.params.subscribe((params) => {
            this._tokenId = params['id'];
        });
    }

    onSubmit() {
        if (this.changeEmailForm.valid) {
            this.loading = true;
            const email = this.changeEmailForm.getRawValue().email ?? '';
            const token = this._tokenId ?? '';

            this.emailService
                .confirmEmail(email, token)
                .subscribe((result: boolean) => {
                    this.loading = false;
                    if (result) {
                        this.toastService.showSuccess(
                            'email.change.success' //todo i18n
                        );
                        this.router.navigate(['/dashboard']);
                    } else {
                        this.toastService.showDanger('email.change.failure'); //todo i18n
                    }
                });
        }
    }
}
