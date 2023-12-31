import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'app-guest-navbar',
    templateUrl: './guest-navbar.component.html'
})
export class GuestNavbarComponent {
    isMenuCollapsed = true;
    isPl = false;

    constructor(protected translate: TranslateService) {
        this.isPl = this.translate.getBrowserLang() == 'pl';
    }

    onClick(language: string) {
        this.translate.use(language);
        this.isPl = language == 'pl';
    }
}
