import { Pipe, PipeTransform } from '@angular/core';
import { Address } from '../model/account';

@Pipe({
    name: 'address'
})
export class AddressPipe implements PipeTransform {
    transform(address: Address): string {
        return `${address.street} ${address.buildingNumber}, ${address.postalCode} ${address.city}`;
    }
}
