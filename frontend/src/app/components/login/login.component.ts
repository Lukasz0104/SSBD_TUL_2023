import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ChooseAccessLevelComponent } from '../modals/choose-access-level/choose-access-level.component';
import { AccessLevel } from '../../model/access-level';
import { ToastService } from '../../services/toast.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent {
    loginForm = new FormGroup({
        login: new FormControl('', [Validators.required]),
        password: new FormControl('', [Validators.required])
    });

    loading = false;

    constructor(
        private authService: AuthService,
        private router: Router,
        private modalService: NgbModal,
        private toastService: ToastService
    ) {}

    onSubmit() {
        if (this.loginForm.valid) {
            this.loading = true;
            const username = this.loginForm.getRawValue().login ?? '';
            const password = this.loginForm.getRawValue().password ?? '';

            this.authService.login(username, password).subscribe(
                (result) => {
                    if (result.status == 200) {
                        this.loading = false;
                        this.toastService.showSuccess('Login successful!');
                        const groupsFromJwt = this.authService.getGroupsFromJwt(
                            result.body?.jwt
                        );
                        if (groupsFromJwt.length > 1) {
                            const modalRef = this.modalService.open(
                                ChooseAccessLevelComponent,
                                {
                                    centered: true
                                }
                            );
                            modalRef.componentInstance.groups = groupsFromJwt;
                            modalRef.result
                                .then((choice) => {
                                    this.notifyServiceAboutLogin(
                                        result,
                                        choice
                                    );
                                    this.router.navigate(['/dashboard']);
                                })
                                .catch(() => {
                                    this.notifyServiceAboutLogin(
                                        result,
                                        groupsFromJwt[0]
                                    );
                                    this.router.navigate(['/dashboard']);
                                });
                        } else {
                            this.notifyServiceAboutLogin(
                                result,
                                groupsFromJwt[0]
                            );
                            this.router.navigate(['/dashboard']);
                        }
                    }
                },
                (error) => {
                    this.loading = false;
                    this.toastService.showDanger('Failed to login.');
                    this.authService.setAuthenticated(false);
                    this.clearPassword();
                }
            );
        }
    }

    clearPassword() {
        this.loginForm.get('password')?.reset();
    }

    notifyServiceAboutLogin(userData: any, group: AccessLevel) {
        this.authService.saveUserData(userData);
        this.authService.setAuthenticated(true);
        this.authService.setCurrentGroup(group);
    }
}
