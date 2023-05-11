import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AccountService } from '../../services/account.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmActionComponent } from '../modals/confirm-action/confirm-action.component';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html'
})
export class DashboardComponent {
    constructor(
        protected router: Router,
        private accountService: AccountService,
        private modalService: NgbModal
    ) {}

    getBreadCrumbs() {
        const url = this.router.url.split('/');
        url.shift();
        const breadcrumbs: string[][] = [];
        url[url.length - 1] = this.removeParams(url[url.length - 1]);

        for (let i = 0; i < url.length; i++) {
            let link = url[i];
            const name = url[i];
            if (i > 0) {
                link = breadcrumbs[i - 1][1] + '/' + link;
            }
            breadcrumbs.push([name, link]);
        }
        return breadcrumbs;
    }

    private removeParams(path: string) {
        const urlDelimitators = new RegExp(/[?,;&:#$+=]/);
        return path.slice(0).split(urlDelimitators)[0];
    }

    forceChange(login: string) {
        const modalRef = this.modalService.open(ConfirmActionComponent, {
            centered: true
        });
        modalRef.componentInstance.message =
            'modal.confirm-action.force-password-message';
        modalRef.componentInstance.danger =
            'modal.confirm-action.force-password-danger';
        modalRef.result.then((result: boolean) => {
            if (result) {
                this.accountService.forcePasswordChange(login).subscribe(() => {
                    console.log('done');
                });
            }
        });
    }
}
