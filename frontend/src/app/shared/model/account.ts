import { AccessType } from './access-type';

export interface OwnAccount {
    accessLevels: AccessLevel[];
    email: string;
    firstName: string;
    id: number;
    language: string;
    lastName: string;
    login: string;
    version: number;
    twoFactorAuth: boolean;
    createdTime?: Date;
    createdBy?: string;
    updatedTime?: Date;
    updatedBy?: string;
    activityTracker: ActivityTracker;
}

export interface EditPersonalData {
    accessLevels: AccessLevel[];
    firstName: string;
    lastName: string;
    login: string;
    version: number;
}

export interface Account extends OwnAccount {
    active: boolean;
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
    createdTime?: Date;
    createdBy?: string;
    updatedTime?: Date;
    updatedBy?: string;
}

export interface Address {
    buildingNumber: number;
    city: string;
    postalCode: string;
    street: string;
}

export interface ActivityTracker {
    unsuccessfulLoginChainCounter: number;
    lastSuccessfulLogin?: Date;
    lastSuccessfulLoginIp?: string;
    lastUnsuccessfulLogin?: Date;
    lastUnsuccessfulLoginIp?: string;
}
