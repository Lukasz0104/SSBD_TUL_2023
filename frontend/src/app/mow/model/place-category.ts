import { AccountingRule } from './accounting-rules';

export interface PlaceCategory {
    rateId: number;
    accountingRule: AccountingRule;
    categoryName: string;
    rate: string;
}
