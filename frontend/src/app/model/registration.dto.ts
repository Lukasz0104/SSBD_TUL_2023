import { Address } from './account';

export interface RegisterAccountDto {
    email: string;
    login: string;
    password: string;
    firstName: string;
    lastName: string;
    language: string;
    captchaCode: string;
}

export interface RegisterOwnerDto extends RegisterAccountDto {
    address: Address;
}

export interface RegisterManagerDto extends RegisterAccountDto {
    address: Address;
    licenseNumber: string;
}
