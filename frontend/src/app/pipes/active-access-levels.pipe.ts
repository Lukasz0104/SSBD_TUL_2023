import { Pipe, PipeTransform } from '@angular/core';
import { AccessLevel } from '../model/account';

@Pipe({
    name: 'activeAccessLevels'
})
export class ActiveAccessLevelsPipe implements PipeTransform {
    transform(accessLevels: AccessLevel[] | undefined): AccessLevel[] {
        return accessLevels?.filter((al) => al.active && al.verified) ?? [];
    }
}
