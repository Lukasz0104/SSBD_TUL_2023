import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { AccessLevel } from '../model/access-level';
import { ToastService } from '../services/toast.service';

export const canActivateAdmin: CanActivateFn = () => {
    const authService = inject(AuthService);
    const toastService = inject(ToastService);
    const router = inject(Router);

    if (authService.getCurrentGroup() !== AccessLevel.ADMIN) {
        router
            .navigate(['/dashboard'])
            .then(() => toastService.showDanger('Access denied'));
        return false;
    }
    return true;
};
