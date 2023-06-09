import { AccountingRule } from '../../shared/model/accounting-rule';

export interface PlaceCategory {
    categoryId: number;
    rateId: number;
    accountingRule: AccountingRule;
    categoryName: string;
    rate: string;
}
