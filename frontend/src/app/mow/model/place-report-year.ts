import { AccountingRule } from './accounting-rule';

export interface PlaceReportYear {
    year: number;
    forecastedCostSum: number;
    totalCostSum: number;
    differential: number;
    details: PlaceCategoryReportYear[];
    balance: number;
}

interface PlaceCategoryReportYear {
    totalCost: number;
    totalConsumption: number;
    categoryName: string;
    accountingRule: AccountingRule;
    forecastAmountSum: number;
    forecastValueSum: number;
    costDifferential: number;
    consumptionDifferential: number;
    averageRate: number;
}
