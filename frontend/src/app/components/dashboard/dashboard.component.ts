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

        if (url.includes('profile')) {
            return [
                new Breadcrumb('/', 'dashboard'),
                new Breadcrumb('', 'profile')
            ];
        }
        if (url.includes('forced-password-override-authenticated')) {
            return [
                new Breadcrumb('/', 'dashboard'),
                new Breadcrumb('', 'override-forced-password')
            ];
        }

        url.shift();
        const breadcrumbs: Breadcrumb[] = [];

        for (let i = 0; i < url.length; i++) {
            let link = url[i];
            let name = url[i];
            if (i === url.length - 1) {
                name = this.removeParams(name);
            }
            if (i > 0) {
                link = breadcrumbs[i - 1].link + '/' + link;
            }
            breadcrumbs.push(new Breadcrumb(link, name));
        }
        return breadcrumbs;
    }

    private removeParams(path: string) {
        const urlDelimitators = new RegExp(/[?,;&:#$+=]/);
        return path.slice(0).split(urlDelimitators)[0];
    }

    protected getKeyForName(name: string): string {
        if (Number.parseInt(name)) {
            return name;
        }
        return `nav.${name}`;
    }
}

class Breadcrumb {
    link: string;
    name: string;

    constructor(link: string, name: string) {
        this.link = link;
        this.name = name;
    }
}
