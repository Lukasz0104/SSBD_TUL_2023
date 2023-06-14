import { AccountingRule } from './accounting-rule';

export interface PlaceReportMonth {
    year: number;
    month: string;
    totalValue: number;
    totalRealValue: number;
    differential: number;
    completeMonth: boolean;
    details: PlaceCategoryReportMonth[];
    balance: number;
}

interface PlaceCategoryReportMonth {
    categoryName: string;
    accountingRule: AccountingRule;
    value: number;
    realValue: number;
    valueDifferential: number;
    amount: number;
    rateValue: number;
}
