import { Cost } from './cost';

export interface CostPage {
    currentPage: number;
    data: Cost[];
    pageSize: number;
    totalSize: number;
}
