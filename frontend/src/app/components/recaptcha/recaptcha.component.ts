import { Component, EventEmitter, Output } from '@angular/core';

@Component({
    selector: 'app-recaptcha',
    templateUrl: './recaptcha.component.html'
})
export class RecaptchaComponent {
    @Output() public captchaResolved = new EventEmitter<string>();

    onResolved(captchaResponse: string) {
        this.captchaResolved.emit(captchaResponse);
    }
}
