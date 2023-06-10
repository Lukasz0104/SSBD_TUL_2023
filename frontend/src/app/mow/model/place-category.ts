import { AccountingRule } from '../../shared/model/accounting-rule';

export interface PlaceCategory {
    rateId: number;
    accountingRule: AccountingRule;
    categoryName: string;
    rate: number;
}

export interface OwnPlaceCategory extends PlaceCategory {
    effectiveDate: Date;
}
