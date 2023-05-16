import { Pipe, PipeTransform } from '@angular/core';
import { AccessLevel } from '../model/account';

@Pipe({
    name: 'activeAccessLevels'
})
export class ActiveAccessLevelsPipe implements PipeTransform {
    transform(accessLevels: AccessLevel[] | undefined): AccessLevel[] {
        if (accessLevels == null) {
            return [];
        }
        accessLevels = accessLevels.filter((al) => al.active && al.verified);
        return accessLevels;
    }
}
