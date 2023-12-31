import { Component } from '@angular/core';
import { AccountService } from '../../shared/services/account.service';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from '../../shared/services/auth.service';
import { ToastService } from '../../shared/services/toast.service';

@Component({
    selector: 'app-change-language',
    templateUrl: './change-language.component.html',
    styleUrls: ['./change-language.component.css']
})
export class ChangeLanguageComponent {
    language = '';

    constructor(
        private accountService: AccountService,
        private translate: TranslateService,
        private authService: AuthService,
        private toastService: ToastService
    ) {
        this.language = this.translate.currentLang.toUpperCase();
    }

    changeLanguage(language: string) {
        if (this.authService.isAuthenticated()) {
            this.accountService
                .changeLanguage(language)
                .subscribe((response) => {
                    if (!response) {
                        this.toastService.showWarning(
                            this.translate.instant(
                                'toast.account.language-fail'
                            )
                        );
                    } else {
                        this.translate.use(language.toLowerCase());
                        localStorage.setItem(
                            'language',
                            language.toLowerCase()
                        );
                        this.language = language;
                    }
                });
        }
    }
}
