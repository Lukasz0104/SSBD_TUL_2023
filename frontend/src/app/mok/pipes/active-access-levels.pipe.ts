import { Pipe, PipeTransform } from '@angular/core';
import { AccessLevel } from '../../shared/model/account';

@Pipe({
    name: 'activeAccessLevels'
})
export class ActiveAccessLevelsPipe implements PipeTransform {
    transform(accessLevels: AccessLevel[] | undefined): AccessLevel[] {
        return (
            accessLevels
                ?.sort((a, b) => (a.level < b.level ? -1 : 1))
                .filter((al) => al.active && al.verified) ?? []
        );
    }
}
