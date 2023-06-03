import { AccountingRule } from '../../shared/model/accounting-rule';

export interface PlaceReportMonth {
    year: number;
    month: string;
    totalValue: number;
    placeCategoryReport: PlaceCategoryReportMonth[];
}

interface PlaceCategoryReportMonth {
    categoryName: string;
    accountingRule: AccountingRule;
    value: number;
    realValue: number;
    amount: number;
    rate_value: number;
}
