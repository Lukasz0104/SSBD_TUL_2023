import { AccountingRule } from '../../shared/model/accounting-rule';

export interface PlaceReportMonth {
    year: number;
    month: string;
    totalValue: number;
    totalRealValue: number;
    differential: number;
    completeMonth: boolean;
    details: PlaceCategoryReportMonth[];
}

interface PlaceCategoryReportMonth {
    categoryName: string;
    accountingRule: AccountingRule;
    value: number;
    realValue: number;
    valueDifferential: number;
    amount: number;
    rate_value: number;
}
