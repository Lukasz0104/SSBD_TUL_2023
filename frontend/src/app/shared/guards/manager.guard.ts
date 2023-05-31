import { CanMatchFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

export const canMatchManager: CanMatchFn = () => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const toastService = inject(ToastService);

    if (!authService.isManager()) {
        router.navigate(['/dashboard']).then(() => {
            toastService.showDanger('toast.guard.access-denied');
        });
        return false;
    }
    return true;
};
