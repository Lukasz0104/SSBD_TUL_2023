import { AccountingRule } from './accounting-rule';

export interface ReportEntry {
    accountingRule: AccountingRule;
    categoryName: string;
    predAmount: number;
    predValue: number;
    realAmount: number;
    realValue: number;
    rate: number;
}

export interface BuildingReport {
    diff: number;
    sumPredValue: number;
    sumRealValue: number;
    categories: ReportEntry[];
}

export interface BuildingReportYearAndMonths {
    months: number[];
    year: number;
}
