import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { ResponseMessage } from '../common/response-message.enum';
import {
    RegisterManagerDto,
    RegisterOwnerDto
} from '../model/registration.dto';

type RegisterResponse = { message: ResponseMessage };

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    private readonly URL = `${environment.apiUrl}/accounts`;

    constructor(private http: HttpClient) {}

    register(
        dto: RegisterOwnerDto | RegisterManagerDto
    ): Observable<ResponseMessage | null> {
        const isManagerDto = 'licenseNumber' in dto && !!dto.licenseNumber;

        return this.http
            .post<RegisterResponse | null>(
                `${this.URL}/register/${isManagerDto ? 'manager' : 'owner'}`,
                dto
            )
            .pipe(
                map((res) => res?.message ?? null),
                catchError((e: HttpErrorResponse) => of(e.error.message))
            );
    }

    confirmRegistration(token: string): Observable<ResponseMessage | null> {
        return this.http
            .post<RegisterResponse | null>(
                `${this.URL}/confirm-registration`,
                null,
                {
                    params: { token }
                }
            )
            .pipe(
                map((res) => res?.message ?? null),
                catchError((e: HttpErrorResponse) => of(e.error.message))
            );
    }
}
