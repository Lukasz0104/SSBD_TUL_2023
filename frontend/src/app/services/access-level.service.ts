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
        const path =
            type === AccessType.ADMIN
                ? 'administrator'
                : type === AccessType.MANAGER
                ? 'manager'
                : 'owner';
        const url = `${this.BASE_URL}/${id}/access-levels/${path}`;

        return this.http.put<MessageResponse | null>(url, level).pipe(
            map((res) => res?.message ?? null),
            catchError((e: HttpErrorResponse) => of(e.error.message))
        );
    }
}
