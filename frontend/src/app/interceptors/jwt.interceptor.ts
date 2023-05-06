import { Injectable } from '@angular/core';
import {
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest
} from '@angular/common/http';
import { catchError, Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { environment } from '../../environments/environment';
import { Router } from '@angular/router';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
    constructor(private authService: AuthService, private router: Router) {}

    intercept(
        request: HttpRequest<unknown>,
        next: HttpHandler
    ): Observable<HttpEvent<unknown>> {
        if (
            request.url.startsWith(environment.apiUrl) &&
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
                        !this.authService.isSessionValid()
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
