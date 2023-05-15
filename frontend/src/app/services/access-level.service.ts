import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { AccessLevel, Address } from '../model/account';
import { AccessType } from '../model/access-type';
import { Observable, catchError, map, of } from 'rxjs';
import { ResponseMessage } from '../common/response-message.enum';

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
    private readonly BASE_URL = `${environment.apiUrl}/accounts`;

    constructor(private http: HttpClient) {}

    grantAccessLevel(
        id: number,
        type: AccessType,
        level: GrantAccessLevel
    ): Observable<null | ResponseMessage> {
        return this.http
            .put<MessageResponse | null>(this.buildUrl(id, type), level)
            .pipe(
                map((res) => res?.message ?? null),
                catchError((e: HttpErrorResponse) => of(e.error.message))
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
            case AccessType.ADMIN:
                return `${this.BASE_URL}/${id}/access-levels/administrator`;
            case AccessType.MANAGER:
                return `${this.BASE_URL}/${id}/access-levels/manager`;
            case AccessType.OWNER:
                return `${this.BASE_URL}/${id}/access-levels/owner`;
            default:
                return '';
        }
    }
}
