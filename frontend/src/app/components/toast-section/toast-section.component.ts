import { Component } from '@angular/core';
import { ToastInfo, ToastService } from '../../shared/services/toast.service';

@Component({
    selector: 'app-toast-section',
    templateUrl: './toast-section.component.html',
    styleUrls: ['./toast-section.component.css']
})
export class ToastSectionComponent {
    constructor(private toastService: ToastService) {}

    removeToast(toast: ToastInfo) {
        this.toastService.remove(toast);
    }

    getToasts() {
        return this.toastService.toasts;
    }
}
