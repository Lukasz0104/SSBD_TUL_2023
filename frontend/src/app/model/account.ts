export interface Account {
    accessLevels: AccessLevel[];
    email: string;
    firstName: string;
    id: number;
    language: string;
    lastName: string;
    login: string;
    version: number;
    active: boolean;
    activityTracker: ActivityTracker;
    verified: boolean;
}

export interface AccessLevel {
    level: string;
    id: number;
    version: number;
    address?: Address;
    licenseNumber?: string;
}

export interface Address {
    buildingNumber: number;
    city: string;
    postalCode: string;
    street: string;
}

export interface ActivityTracker {
    unsuccessfulLoginChainCounter: number;
    lastSuccessfulLogin?: string;
    lastSuccessfulLoginIp?: string;
}
