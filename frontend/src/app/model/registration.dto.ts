import { Address } from './address.model';

interface RegisterAccountDto {
    email: string;
    login: string;
    password: string;
    repeatPassword: string;
    firstName: string;
    lastName: string;
    language: string;
}

export interface RegisterOwnerDto extends RegisterAccountDto {
    address: Address;
}

export interface RegisterManagerDto extends RegisterAccountDto {
    address: Address;
    licenseNumber: string;
}
