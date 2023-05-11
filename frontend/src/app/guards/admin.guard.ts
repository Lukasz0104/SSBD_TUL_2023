import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';
import { AccessType } from '../model/access-type';

export const canActivateAdmin: CanActivateFn = () => {
    const authService = inject(AuthService);
    const toastService = inject(ToastService);
    const router = inject(Router);

    if (authService.getCurrentGroup() !== AccessType.ADMIN) {
        router
            .navigate(['/dashboard'])
            .then(() => toastService.showDanger('toast.guard.access-denied'));
        return false;
    }
    return true;
};
