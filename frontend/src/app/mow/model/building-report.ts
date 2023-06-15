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
    balance: number;
    categories: ReportEntry[];
}

export interface BuildingReportYearAndMonths {
    months: number[];
    year: number;
}

export interface CommunityReport {
    balance: number;
    reportsPerCategory: ReportEntry[];
}
