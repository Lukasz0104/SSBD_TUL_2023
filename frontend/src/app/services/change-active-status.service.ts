import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { catchError, map, of } from 'rxjs';
import { ActiveStatusDto } from '../model/active-status-dto';

@Injectable({ providedIn: 'root' })
export class ChangeActiveStatusService {
    constructor(private http: HttpClient) {}

    changeActiveStatusAsManager(id: number, bool: boolean) {
        return this.http
            .put<ActiveStatusDto>(
                `${environment.apiUrl}/manager/change-active-status`,
                {
                    id,
                    bool
                }
            )
            .pipe(
                map(() => {
                    return true;
                }),
                catchError(() => of(false))
            );
    }

    changeActiveStatusAsAdmin(id: number, bool: boolean) {
        return this.http
            .put<ActiveStatusDto>(
                `${environment.apiUrl}/admin/change-active-status`,
                {
                    id,
                    bool
                }
            )
            .pipe(
                map(() => {
                    return true;
                }),
                catchError(() => of(false))
            );
    }
}
