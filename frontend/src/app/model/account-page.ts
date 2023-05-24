import { Account } from './account';

export interface AccountPage {
    currentPage: number;
    data: Account[];
    pageSize: number;
    totalSize: number;
}
