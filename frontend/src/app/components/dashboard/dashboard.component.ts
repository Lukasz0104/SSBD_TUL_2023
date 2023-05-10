import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html'
})
export class DashboardComponent {
    constructor(protected router: Router) {}

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
}
