import { Account } from './account';
import { Page } from './page';

export interface AccountPage extends Page {
    data: Account[];
}
