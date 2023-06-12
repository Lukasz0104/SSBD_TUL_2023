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
