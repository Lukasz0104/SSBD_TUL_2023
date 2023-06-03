import { AccountingRule } from '../../shared/model/accounting-rule';

export interface PlaceReportYear {
    year: number;
    forecastedCostSum: number;
    totalCostSum: number;
    placeCategoryReport: PlaceCategoryReportYear[];
}

interface PlaceCategoryReportYear {
    year: number;
    totalCost: number;
    totalConsumption: number;
    categoryName: number;
    accountingRule: AccountingRule;
    forecastAmountSum: number;
    forecastValueSum: number;
}
