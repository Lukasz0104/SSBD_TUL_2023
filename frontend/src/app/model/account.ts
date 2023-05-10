import { AccessType } from './access-type';

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
    level: AccessType;
    id: number;
    version: number;
    address?: Address;
    licenseNumber?: string;
    verified: boolean;
    active: boolean;
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
