import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'truncate'
})
export class TruncatePipe implements PipeTransform {
    transform(text: string | null, amount: number): string {
        if (text === null) {
            return '';
        }
        if (text.length > amount) {
            return text.slice(0, amount) + '...';
        }
        return text;
    }
}
