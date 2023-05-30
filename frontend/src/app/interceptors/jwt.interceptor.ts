import { Injectable } from '@angular/core';
import {
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest
} from '@angular/common/http';
import { catchError, Observable } from 'rxjs';
import { AuthService } from '../shared/services/auth.service';
import { Router } from '@angular/router';
import { ToastService } from '../shared/services/toast.service';
import { AppConfigService } from '../shared/services/app-config.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
    constructor(
        private authService: AuthService,
        private router: Router,
        private toastService: ToastService,
        private appConfig: AppConfigService
    ) {}

    intercept(
        request: HttpRequest<unknown>,
        next: HttpHandler
    ): Observable<HttpEvent<unknown>> {
        if (
            request.url.startsWith(this.appConfig.apiUrl) &&
            this.authService.isAuthenticated()
        ) {
            const cloned = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${this.authService.getJwt()}`
                }
            });
            return next.handle(cloned).pipe(
                catchError((err) => {
                    if (
                        err.status === 403 &&
                        !this.authService.isJwtValid(this.authService.getJwt())
                    ) {
                        this.logout();
                    }
                    throw err;
                })
            );
        }
        return next.handle(request);
    }

    logout() {
        this.authService.clearUserData();
        this.authService.setAuthenticated(false);
        this.router.navigate(['/login']);
    }
}
