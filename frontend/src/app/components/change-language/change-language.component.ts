import { Component } from '@angular/core';
import { AccountService } from '../../services/account.service';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-change-language',
    templateUrl: './change-language.component.html',
    styleUrls: ['./change-language.component.css']
})
export class ChangeLanguageComponent {
    language = 'PL';

    constructor(
        private accountService: AccountService,
        private translate: TranslateService,
        private authService: AuthService
    ) {
        this.language = this.translate.getBrowserLang()!.toUpperCase();
    }

    changeLanguage(language: string) {
        if (this.authService.isAuthenticated()) {
            this.accountService.changeLanguage(language);
        }
        this.translate.use(language.toLowerCase());
        this.language = language;
    }
}
