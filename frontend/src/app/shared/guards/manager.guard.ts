import { inject } from '@angular/core';
import { CanActivateFn, CanMatchFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

export const canMatchManager: CanMatchFn = () => {
    const authService = inject(AuthService);
    const toastService = inject(ToastService);
    const router = inject(Router);

    if (!authService.isManager()) {
        router
            .navigate(['/dashboard'])
            .then(() => toastService.showDanger('toast.guard.access-denied'));
        return false;
    }
    return true;
};

export const canActivateManager: CanActivateFn = () => {
    const authService = inject(AuthService);
    const toastService = inject(ToastService);
    const router = inject(Router);

    if (!authService.isManager()) {
        router
            .navigate(['/dashboard'])
            .then(() => toastService.showDanger('toast.guard.access-denied'));
        return false;
    }
    return true;
};
