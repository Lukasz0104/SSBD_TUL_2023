import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AccessLevel, Address } from '../../shared/model/account';
import { AccessLevels, AccessType } from '../../shared/model/access-type';
import { catchError, EMPTY, map, Observable, of } from 'rxjs';
import { ResponseMessage } from '../../shared/model/response-message.enum';
import { ToastService } from '../../shared/services/toast.service';
import { AppConfigService } from '../../shared/services/app-config.service';

type GrantAccessLevel =
    | AccessLevel
    | {
          address: Address;
          licenseNumber?: string;
      }
    | undefined;

type MessageResponse = { message: ResponseMessage };

@Injectable({
    providedIn: 'root'
})
export class AccessLevelService {
    private readonly BASE_URL = `${this.appConfig.apiUrl}/accounts`;

    constructor(
        private http: HttpClient,
        private toastService: ToastService,
        private appConfig: AppConfigService
    ) {}

    grantAccessLevel(
        id: number,
        type: AccessType,
        level: GrantAccessLevel
    ): Observable<null | ResponseMessage> {
        return this.http
            .put<MessageResponse | null>(this.buildUrl(id, type), level)
            .pipe(
                map((res) => res?.message ?? null),
                catchError((e: HttpErrorResponse) => {
                    if (e.error.message == null || e.status == 500) {
                        this.toastService.showDanger(
                            'toast.grant-access-level-fail'
                        );
                    } else {
                        this.toastService.showDanger(
                            'grant-access-level.' + e.error.message
                        );
                    }
                    return EMPTY;
                })
            );
    }

    reject(id: number, type: AccessType) {
        return this.http.delete(this.buildUrl(id, type)).pipe(
            map(() => true),
            catchError(() => of(false))
        );
    }

    private buildUrl(id: number, type: AccessType) {
        switch (type) {
            case AccessLevels.ADMIN:
                return `${this.BASE_URL}/${id}/access-levels/administrator`;
            case AccessLevels.MANAGER:
                return `${this.BASE_URL}/${id}/access-levels/manager`;
            case AccessLevels.OWNER:
                return `${this.BASE_URL}/${id}/access-levels/owner`;
            default:
                return '';
        }
    }
}
