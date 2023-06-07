import { Component, Input } from '@angular/core';
import { Observable } from 'rxjs';
import { Account } from '../../../shared/model/account';

@Component({
    selector: 'app-place-edit',
    templateUrl: './place-edit.component.html'
})
export class PlaceEditComponent {
    @Input() public account$: Observable<Account | null> | undefined;
    public newAccount: Account | null | undefined;
}
