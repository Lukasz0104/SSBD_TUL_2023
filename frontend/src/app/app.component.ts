import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import plLocale from './../assets/i18n/pl.json';
import enLocale from './../assets/i18n/en.json';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {
    title = 'eBok';

    constructor(private translate: TranslateService) {
        const browserLanguage = translate.getBrowserLang()!;
        if (browserLanguage == 'pl') {
            translate.setTranslation(browserLanguage, plLocale);
        } else {
            translate.setTranslation(browserLanguage, enLocale);
        }
        translate.setDefaultLang(browserLanguage);
    }
}
