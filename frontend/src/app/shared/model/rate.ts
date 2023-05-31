import { AccountingRule } from './accounting-rule';
import { ControlledEntity } from './controlled-entity';

export interface Rate extends ControlledEntity {
    id: number;
    effectiveDate: Date;
    accountingRule: AccountingRule;
    value: number;
}

export interface PublicRate {
    categoryName: string;
    accountingRule: AccountingRule;
    value: number;
}
