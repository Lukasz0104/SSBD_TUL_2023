export interface Page<T> {
    data: T[];
    currentPage: number;
    pageSize: number;
    totalSize: number;
}
