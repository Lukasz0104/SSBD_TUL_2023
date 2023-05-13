export enum AccessType {
    OWNER = 'OWNER',
    MANAGER = 'MANAGER',
    ADMIN = 'ADMIN',
    NONE = '',
    ALL = 'ALL'
}

export interface ChangeAccessLevelDto {
    accessType: AccessType;
}
