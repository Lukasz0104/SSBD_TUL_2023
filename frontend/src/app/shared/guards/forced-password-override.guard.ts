import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';

export function canActivateForcedPasswordOverride(
    route: ActivatedRouteSnapshot
): boolean {
    const router = inject(Router);
    const authService = inject(AuthService);
    if (authService.isAuthenticated()) {
        router.navigate([
            'dashboard/forced-password-override-authenticated/' +
                route.paramMap.get('token')
        ]);
        return true;
    }
    return true;
}
