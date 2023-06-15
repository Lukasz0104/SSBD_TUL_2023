import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

export interface ToastInfo {
    body: string;
    className: string;
    delay?: number;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
    toasts: ToastInfo[] = [];

    showDanger(body: string) {
        this.toasts.push({ body, className: 'text-bg-danger' });
    }

    showSuccess(body: string) {
        this.toasts.push({ body, className: 'text-bg-success' });
    }

    showWarning(body: string) {
        this.toasts.push({ body, className: 'text-bg-warning' });
    }

    showInfo(body: string) {
        this.toasts.push({ body, className: 'text-bg-info' });
    }

    remove(toast: ToastInfo) {
        this.toasts = this.toasts.filter((t) => t != toast);
    }

    clearAll() {
        this.toasts = [];
    }

    handleError(
        genericMessageKey: string,
        method: string,
        response: HttpErrorResponse
    ) {
        if (response.status == 500 || response.error.message == null) {
            this.showDanger(genericMessageKey);
        } else {
            this.showDanger(method + '.' + response.error.message);
        }
    }
}
