export interface Meter {
    id: number;
    category: string;
    hasReadingInLast30Days: boolean;
    dateOfNextReading: string;
    active: boolean;
}
