import { Injectable } from '@angular/core';

export interface ToastInfo {
    body: string;
    className: string;
    delay?: number;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
    toasts: ToastInfo[] = [];

    showDanger(body: string) {
        this.toasts.push({ body, className: 'bg-danger text-light' });
    }

    showSuccess(body: string) {
        this.toasts.push({ body, className: 'bg-success text-light' });
    }

    showWarning(body: string) {
        this.toasts.push({ body, className: 'bg-warning' });
    }

    showInfo(body: string) {
        this.toasts.push({ body, className: 'bg-info' });
    }

    remove(toast: ToastInfo) {
        this.toasts = this.toasts.filter((t) => t != toast);
    }

    clearAll() {
        this.toasts = [];
    }
}
