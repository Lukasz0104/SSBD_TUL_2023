import { AccountingRule } from '../../shared/model/accounting-rule';

export interface PlaceCategory {
    rateId: number;
    accountingRule: AccountingRule;
    categoryName: string;
    rate: string;
}
