export interface CostPage {
    currentPage: number;
    data: Cost[];
    pageSize: number;
    totalSize: number;
}

export interface Cost {
    id: number;
    year: number;
    month: string;
    totalConsumption: number;
    realRate: number;
    category: string;
    createdTime?: Date;
    createdBy?: string;
    updatedTime?: Date;
    updatedBy?: string;
}
